package com.njp.wallhaven3.repositories.bean

data class DetailImageInfo(
        var url: String,
        var resolution: String,
        var tags: List<Tag>
)