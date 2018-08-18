package com.njp.wallhaven.repositories.bean

data class DetailImageInfo(
        var url: String,
        var resolution: String,
        var tags: List<Tag>
)