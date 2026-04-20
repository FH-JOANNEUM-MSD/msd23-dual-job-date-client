package fh.msd.jobdating.feature.companies.ui.components

import dualjobdating.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

object CompanyImageProvider {
    private val buildings = listOf(
        Res.drawable._1,
        Res.drawable._2,
        Res.drawable._3,
        Res.drawable._4,
        Res.drawable._5,
        Res.drawable._6,
        Res.drawable._7
    )

    private val offices = listOf(
        Res.drawable._11,
        Res.drawable._22,
        Res.drawable._33,
        Res.drawable._44,
        Res.drawable._55,
        Res.drawable._66,
        Res.drawable._77
    )

    fun getFallbackImages(companyId: Int): List<DrawableResource> {
        val buildingIndex = companyId % 7
        val office1Index = (companyId * 2) % 7
        val office2Index = (companyId * 3) % 7

        return listOf(
            buildings[buildingIndex],
            offices[office1Index],
            offices[office2Index]
        )
    }
}