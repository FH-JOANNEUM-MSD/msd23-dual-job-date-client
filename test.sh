#!/bin/bash

BACKEND_URL="https://dual-job-dating-backend.msd-moss-prod.fh-joanneum.at"
SUPABASE_URL="https://dtgigetmxmrqniibjsal.supabase.co"
SUPABASE_ANON_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR0Z2lnZXRteG1ycW5paWJqc2FsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI2MTQxNDMsImV4cCI6MjA4ODE5MDE0M30.DV_NZ7trFcTIcm8Hb_pdkv3cDvJSaJpd7KnF3twtL1s"

EMAIL="robb.stark@westeros.com"
PASSWORD="WinterIsComing123!"

echo "=== Login ==="
LOGIN_RESPONSE=$(curl -s -X POST "${SUPABASE_URL}/auth/v1/token?grant_type=password" \
  -H "apikey: ${SUPABASE_ANON_KEY}" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"${EMAIL}\",\"password\":\"${PASSWORD}\"}")

ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"access_token":"[^"]*' | sed 's/"access_token":"//')

if [ -z "$ACCESS_TOKEN" ]; then
    echo "Login failed!"
    exit 1
fi

echo "Logged in successfully"
echo ""

echo "=== Fetching All Students ==="
STUDENTS=$(curl -s -X GET "${BACKEND_URL}/api/students" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json")

echo "$STUDENTS"
echo ""

STUDENT_IDS=$(echo "$STUDENTS" | grep -o '"id":[0-9]*' | sed 's/"id"://' | head -15)

echo "=== Checking Preferences for Each Student ==="
echo ""

for STUDENT_ID in $STUDENT_IDS; do
    echo "--- Student ID: $STUDENT_ID ---"

    PREFS=$(curl -s -X GET "${BACKEND_URL}/api/students/${STUDENT_ID}/preferences" \
      -H "Authorization: Bearer ${ACCESS_TOKEN}" \
      -H "Content-Type: application/json")

    PREF_COUNT=$(echo "$PREFS" | grep -o '"id":[0-9]*' | wc -l)
    NEUTRAL_COUNT=$(echo "$PREFS" | grep -o '"preference_type":"neutral"' | wc -l)
    LIKE_COUNT=$(echo "$PREFS" | grep -o '"preference_type":"like"' | wc -l)
    DISLIKE_COUNT=$(echo "$PREFS" | grep -o '"preference_type":"dislike"' | wc -l)

    echo "  Total preferences: $PREF_COUNT"
    echo "  Neutral: $NEUTRAL_COUNT"
    echo "  Like: $LIKE_COUNT"
    echo "  Dislike: $DISLIKE_COUNT"
    echo ""
done

echo "=== Summary ==="
echo "If ALL students have 8 neutral preferences, the backend is creating default neutral votes."
echo "If students have 0 preferences, the backend is working correctly."