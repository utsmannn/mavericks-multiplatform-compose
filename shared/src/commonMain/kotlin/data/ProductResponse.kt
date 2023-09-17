package data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    @SerialName("status")
    val status: Boolean?,
    @SerialName("message")
    val message: String?,
    @SerialName("data")
    val `data`: DataResponse?
) {
    @Serializable
    data class DataResponse(
        @SerialName("per_page")
        val perPage: Int?,
        @SerialName("has_next_page")
        val hasNextPage: Boolean?,
        @SerialName("page")
        val page: Int?,
        @SerialName("data")
        val `data`: List<DataResponse?>?
    ) {
        @Serializable
        data class DataResponse(
            @SerialName("id")
            val id: Int?,
            @SerialName("name")
            val name: String?,
            @SerialName("description")
            val description: String?,
            @SerialName("category")
            val category: CategoryResponse?,
            @SerialName("price")
            val price: Double?,
            @SerialName("images")
            val images: List<String?>?,
            @SerialName("brand")
            val brand: BrandResponse?,
            @SerialName("promoted")
            val promoted: Boolean?
        ) {
            @Serializable
            data class CategoryResponse(
                @SerialName("id")
                val id: Int?,
                @SerialName("name")
                val name: String?
            )

            @Serializable
            data class BrandResponse(
                @SerialName("id")
                val id: Int?,
                @SerialName("name")
                val name: String?,
                @SerialName("logo")
                val logo: String?
            )
        }
    }
}