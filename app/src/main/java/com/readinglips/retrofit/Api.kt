package com.withsejong.retrofit

import androidx.camera.core.DynamicRange.BitDepth
import com.google.gson.JsonElement
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface Api {
    @POST("/signup")
    fun signup(
        @Body jsonParams: JsonElement
    ): Call<SignupResponse>

    @POST("/login")
    fun login(
        @Body jsonParams: JsonElement
    ): Call<LoginResponse>

    @GET("/pronunciation/text")
    fun loadText(

    ):Call<LoadTextResponse>

    @POST("/pronunciation")
    fun uploadPronunciationTestVideo(
        @Body jsonParams: JsonElement

    ):Call<UploadPronunciationTestVideoResponse>

    @GET("/pronunciation")
    fun loadPronunciationHistory(
        @Query("userEmail") userEmail : String
    ):Call<ArrayList<LoadPronunciationHistoryResponse>>


    @GET("/subtitle")
    fun loadLipReadingHistory(
        @Query("userEmail") userEmail : String
    ):Call<ArrayList<LoadLipReadingHistoryResponse>>

    @GET("/user/email/verification-request")
    fun sendEmailCode(
        @Query("email") email:String,
        @Query("type") type:Int
    ):Call<SendEmailCodeResponse>

    @POST("/user/email/verification")
    fun checkEmailCode(
        @Body jsonParams: JsonElement
    ):Call<Boolean>

    @POST("/change-password")
    fun changePw(
        @Body jsonParams: JsonElement
    ):Call<ChangePwResponse>


    @GET("/pronunciation")
    fun getPronunciationList(
        @Query("userEmail") userEmail: String
    ):Call<ArrayList<LoadPronunciationHistoryResponse>>

    @POST("/subtitle")
    fun uploadSubtitle(
        @Body jsonParams: JsonElement
    ):Call<UploadSubtitleResponse>



    //아래 api는 모두 지울 것

    //http://43.201.66.172:8080/http://12.12.12.12:8080/checkNickname?nickname=misterjerry
//    @GET("/check-nickname")
//    fun isDuplicatedNickname(
//        @Query("nickname") nickname: String
//    ): Call<Boolean>
//
//    @GET("/check-student-id")
//    fun isDuplicatedID(
//        @Query("studentId") id: String
//    ): Call<CheckStudentIdResponse>
//
//    @PUT("/change-forget-password")
//    fun changeForgotPassword(
//        //@Path("password") password:String,
//        @Body jsonParams: JsonElement
//    ): Call<ChangeForgotPassword>
//
//    @DELETE("/user/delete")
//    fun deleteAccount(
//        @Header("Authorization") accessToken: String,
//        @Query("studentId") studentId: String
//    ): Call<DeleteAccountResponse>
//
//    @PUT("/user/update")
//    fun updateUserInfo(
//        @Header("Authorization") accessToken: String,
//        @Body jsonParams: JsonElement
//    ): Call<UpdateUserInfoResponse>
//
//    @GET("/user/faq")
//    fun loadFaq(
//        @Header("Authorization") accessToken: String
//    ): Call<ArrayList<LoadFaqResponse>>
//
//
//    @POST("/refresh")
//    fun refreshToken(
//        @Header("Authorization") accessToken: String,
//        @Body jsonParams: JsonElement
//    ): Call<RefreshTokenResponse>
//
//    @GET("/user/logout")
//    fun logout(
//        @Header("Authorization") accessToken: String,
//        @Query("studentId") studentId: String
//    ): Call<LogoutResponse>
//
//    @GET("/find-nickname")
//    fun findNickname(
//        @Query("studentId") studentId: String
//    ): Call<String>
//
//    @Multipart
//    @POST("/user/board/save")
//    fun makePost(
//        @Header("Authorization") accessToken: String,
//        @Part("request") request : RequestBody,
//        @Part file : List<MultipartBody.Part>
//        ):Call<MakePostResponse>
//
//    @GET("/user/board")
//    //계속 로드하면 서버에 무리되어서 일단 주소에 1 붙임
//    fun loadPost(
//        @Header("Authorization") accessToken: String,
//        @Query("page") page:Int
//        ):Call<LoadPostResponse>
//
//    @GET("/user/board/search")
//    fun loadSearchPost(
//        @Header("Authorization") accessToken: String,
//        @Query("keyword") keyword:String,
//        @Query("page") page:Int,
//        ):Call<LoadPostResponse>
//
//    @GET("/user/board/history")
//    fun loadSellList(
//        @Header("Authorization") accessToken: String,
//        @Query("studentId") studentId: String,
//        @Query("page") page:Int
//    ):Call<LoadPostResponse>

}