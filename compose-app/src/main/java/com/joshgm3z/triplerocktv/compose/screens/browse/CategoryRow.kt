package com.joshgm3z.triplerocktv.compose.screens.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.joshgm3z.triplerocktv.compose.screens.browse.uistate.SectionTitle
import com.joshgm3z.triplerocktv.compose.screens.common.DarkPreview
import com.joshgm3z.triplerocktv.compose.screens.common.DarkSurface
import com.joshgm3z.triplerocktv.compose.screens.common.listSpacing
import com.joshgm3z.triplerocktv.compose.theme.cardColor
import com.joshgm3z.triplerocktv.compose.theme.subTextColor
import com.joshgm3z.triplerocktv.compose.theme.textColor
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.util.withComma

@Composable
fun CategoryRow(
    title: String = "Malayalam",
    categories: List<CategoryData> = emptyList(),
    sidePadding: Dp = 10.dp,
    listHorizontalPadding: Dp = 5.dp,
    onCategoryClick: (CategoryData) -> Unit = {},
) {
    Column {
        SectionTitle(title = title)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(listHorizontalPadding)
        ) {
            listSpacing(sidePadding)
            items(categories) {
                CategoryItem(categoryData = it) {
                    onCategoryClick(it)
                }
            }
            listSpacing(sidePadding)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    categoryData: CategoryData,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .width(185.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = cardColor())
            .clickable(true) { onClick() }
            .padding(10.dp),
    ) {
        Text(
            text = categoryData.categoryName,
            style = typography.titleSmall,
            color = textColor(),
            maxLines = 2,
        )
        Spacer(Modifier.size(1.dp))
        Text(
            text = "${categoryData.count.withComma()} items",
            style = typography.bodyMedium,
            color = subTextColor(),
            maxLines = 1,
        )
    }
}

@DarkPreview
@Composable
private fun PreviewCategoryItem() {
    DarkSurface {
        CategoryItem(
            categoryData = CategoryData(
                categoryName = "OSCAR WINNING MOVIES NOW",
                categoryId = 1,
                parentId = 1,
                count = 4,
                streamType = StreamType.VideoOnDemand
            ),
            onClick = {}
        )
    }
}

@DarkPreview
@Composable
private fun PreviewCategoryRow() {
    DarkSurface {
        CategoryRow(
            categories = listOf(
                CategoryData(
                    categoryName = "OSCAR WINNING MOVIES NOW",
                    categoryId = 1,
                    parentId = 1,
                    count = 4,
                    streamType = StreamType.VideoOnDemand
                ),
                CategoryData(
                    categoryName = "MALAYALAM",
                    categoryId = 1,
                    count = 4500,
                    parentId = 1,
                    streamType = StreamType.VideoOnDemand
                )
            )
        )
    }
}