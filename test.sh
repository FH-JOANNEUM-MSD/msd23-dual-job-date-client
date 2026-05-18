#!/bin/bash

BACKEND_URL="https://dual-job-dating-backend.msd-moss-prod.fh-joanneum.at"
SUPABASE_URL="https://dtgigetmxmrqniibjsal.supabase.co"
SUPABASE_ANON_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR0Z2lnZXRteG1ycW5paWJqc2FsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI2MTQxNDMsImV4cCI6MjA4ODE5MDE0M30.DV_NZ7trFcTIcm8Hb_pdkv3cDvJSaJpd7KnF3twtL1s"

EMAIL="robb.stark@westeros.com"
PASSWORD="WinterIsComing123!"

SUMMARY_ENDPOINTS=()
SUMMARY_CODES=()

record() {
    SUMMARY_ENDPOINTS+=("$1")
    SUMMARY_CODES+=("$2")
}

echo "=========================================="
echo "BACKEND ENDPOINT TEST"
echo "=========================================="
echo ""

echo "=== LOGIN ==="
echo "POST ${SUPABASE_URL}/auth/v1/token?grant_type=password"
LOGIN_RESPONSE=$(curl -s -X POST "${SUPABASE_URL}/auth/v1/token?grant_type=password" \
  -H "apikey: ${SUPABASE_ANON_KEY}" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"${EMAIL}\",\"password\":\"${PASSWORD}\"}")

ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"access_token":"[^"]*' | sed 's/"access_token":"//')

if [ -z "$ACCESS_TOKEN" ]; then
    echo "FAILED - could not retrieve access token"
    record "POST /auth/v1/token" "FAIL"
    exit 1
fi

echo "SUCCESS"
record "POST /auth/v1/token" "200"
echo ""

echo "=========================================="
echo "ENDPOINT: GET ${BACKEND_URL}/api/me"
echo "=========================================="
ME_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BACKEND_URL}/api/me" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$ME_RESPONSE" | grep "HTTP_CODE" | sed 's/HTTP_CODE://')
BODY=$(echo "$ME_RESPONSE" | sed '/HTTP_CODE/d')
record "GET /api/me" "$HTTP_CODE"

echo "HTTP Status: $HTTP_CODE"
echo "Response:"
echo "$BODY" | jq . 2>/dev/null || echo "$BODY"

STUDENT_ID=$(echo "$BODY" | grep -o '"student_id":[0-9]*' | sed 's/"student_id"://')
USER_ID=$(echo "$BODY" | grep -o '"user_id":"[^"]*' | sed 's/"user_id":"//' | sed 's/".*//')
echo ""
echo "Extracted: student_id=$STUDENT_ID, user_id=$USER_ID"
echo ""

echo "=========================================="
echo "ENDPOINT: GET ${BACKEND_URL}/api/companies/active"
echo "=========================================="
COMPANIES=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BACKEND_URL}/api/companies/active" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$COMPANIES" | grep "HTTP_CODE" | sed 's/HTTP_CODE://')
BODY=$(echo "$COMPANIES" | sed '/HTTP_CODE/d')
record "GET /api/companies/active" "$HTTP_CODE"

echo "HTTP Status: $HTTP_CODE"
COMPANY_COUNT=$(echo "$BODY" | grep -o '"id":[0-9]*' | wc -l)
echo "Companies returned: $COMPANY_COUNT"

FIRST_COMPANY_ID=$(echo "$BODY" | grep -o '"id":[0-9]*' | head -1 | sed 's/"id"://')
echo "First company id: $FIRST_COMPANY_ID"
echo ""

echo "=========================================="
echo "ENDPOINT: GET ${BACKEND_URL}/api/companies/${FIRST_COMPANY_ID}"
echo "=========================================="
COMPANY=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BACKEND_URL}/api/companies/${FIRST_COMPANY_ID}" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$COMPANY" | grep "HTTP_CODE" | sed 's/HTTP_CODE://')
BODY=$(echo "$COMPANY" | sed '/HTTP_CODE/d')
record "GET /api/companies/${FIRST_COMPANY_ID}" "$HTTP_CODE"

echo "HTTP Status: $HTTP_CODE"
echo "Response:"
echo "$BODY" | jq . 2>/dev/null || echo "$BODY"
echo ""

echo "=========================================="
echo "ENDPOINT: POST ${BACKEND_URL}/api/companies/4/vote"
echo "Body: {\"vote\":\"like\"}"
echo "=========================================="
VOTE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST "${BACKEND_URL}/api/companies/4/vote" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"vote":"like"}')

HTTP_CODE=$(echo "$VOTE" | grep "HTTP_CODE" | sed 's/HTTP_CODE://')
BODY=$(echo "$VOTE" | sed '/HTTP_CODE/d')
record "POST /api/companies/4/vote" "$HTTP_CODE"

echo "HTTP Status: $HTTP_CODE"
echo "Response:"
echo "$BODY" | jq . 2>/dev/null || echo "$BODY"
echo ""

echo "=========================================="
echo "ENDPOINT: GET ${BACKEND_URL}/api/students/${STUDENT_ID}/preferences"
echo "=========================================="
PREFS=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BACKEND_URL}/api/students/${STUDENT_ID}/preferences" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$PREFS" | grep "HTTP_CODE" | sed 's/HTTP_CODE://')
BODY=$(echo "$PREFS" | sed '/HTTP_CODE/d')
record "GET /api/students/${STUDENT_ID}/preferences" "$HTTP_CODE"

echo "HTTP Status: $HTTP_CODE"
echo "Response:"
echo "$BODY" | jq . 2>/dev/null || echo "$BODY"
PREF_COUNT=$(echo "$BODY" | grep -o '"id":[0-9]*' | wc -l)
echo "Preferences returned: $PREF_COUNT"
echo ""

echo "=========================================="
echo "ENDPOINT: GET ${BACKEND_URL}/api/students/${STUDENT_ID}/meetings"
echo "=========================================="
MEETINGS=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BACKEND_URL}/api/students/${STUDENT_ID}/meetings" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$MEETINGS" | grep "HTTP_CODE" | sed 's/HTTP_CODE://')
BODY=$(echo "$MEETINGS" | sed '/HTTP_CODE/d')
record "GET /api/students/${STUDENT_ID}/meetings" "$HTTP_CODE"

echo "HTTP Status: $HTTP_CODE"
echo "Response:"
echo "$BODY" | jq . 2>/dev/null || echo "$BODY"
MEETING_COUNT=$(echo "$BODY" | grep -o '"id":[0-9]*' | wc -l)
echo "Meetings returned: $MEETING_COUNT"
echo ""

echo "=========================================="
echo "ENDPOINT: GET ${BACKEND_URL}/api/events/active"
echo "=========================================="
EVENTS=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BACKEND_URL}/api/events/active" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$EVENTS" | grep "HTTP_CODE" | sed 's/HTTP_CODE://')
BODY=$(echo "$EVENTS" | sed '/HTTP_CODE/d')
record "GET /api/events/active" "$HTTP_CODE"

echo "HTTP Status: $HTTP_CODE"
echo "Response:"
echo "$BODY" | jq . 2>/dev/null || echo "$BODY"
echo ""

echo "=========================================="
echo "ENDPOINT: GET ${BACKEND_URL}/api/slots"
echo "=========================================="
SLOTS=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BACKEND_URL}/api/slots" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json")

HTTP_CODE=$(echo "$SLOTS" | grep "HTTP_CODE" | sed 's/HTTP_CODE://')
BODY=$(echo "$SLOTS" | sed '/HTTP_CODE/d')
record "GET /api/slots" "$HTTP_CODE"

echo "HTTP Status: $HTTP_CODE"
SLOT_COUNT=$(echo "$BODY" | grep -o '"id":[0-9]*' | wc -l)
echo "Slots returned: $SLOT_COUNT"
echo ""

echo "=========================================="
echo "SUMMARY"
echo "=========================================="

PASS=0
FAIL=0

for i in "${!SUMMARY_ENDPOINTS[@]}"; do
    ENDPOINT="${SUMMARY_ENDPOINTS[$i]}"
    CODE="${SUMMARY_CODES[$i]}"
    if [[ "$CODE" == "200" || "$CODE" == "201" ]]; then
        echo "  OK   [$CODE]  $ENDPOINT"
        PASS=$((PASS + 1))
    else
        echo "  FAIL [$CODE]  $ENDPOINT"
        FAIL=$((FAIL + 1))
    fi
done

echo ""
echo "Result: $PASS passed, $FAIL failed out of $((PASS + FAIL)) endpoints"
echo ""