package com.withsejong.retrofit


data class SignupResponse(
    val name: String,
    val email:String,
    val password:String
)

data class LoginResponse(
    val email:String,
    val password: String,
    val authToken: LoginResponseAuthToken
)
data class LoginResponseAuthToken(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
)


data class LoadTextResponse(
    val originalText:String
)

data class UploadPronunciationTestVideoResponse(
    val originalText : String,
    val pronunciationText : String,
    val accuracy : Int
)

data class LoadPronunciationHistoryResponse(
    val originalText: String,
    val pronunciationText: String,
    val accuracy: Double
)

data class LoadLipReadingHistoryResponse(
    val subtitle:String,
    val createdAt:String
)

data class SendEmailCodeResponse(
    val email:String,
    val code:String,
    val createdAt : String
)

data class ChangePwResponse(
    val email: String
)



//
//data class ChangeForgotPassword(
//    val studentId: String,
//    val nickname: String
//)
//
//data class DeleteAccountResponse(
//    val studentId: String,
//    val name:String
//)
//
//data class CheckStudentIdResponse(
//    val isSigned:Boolean?,
//    val isDeleted:Boolean?
//)
//
//data class UpdateUserInfoResponse(
//    val studentId: String,
//    val nickname: String,
//    val major: String
//)
//data class LoadFaqResponse(
//    val title:String,
//    val context : String
//)
//
//data class RefreshTokenResponse(
//    val grantType: String,
//    val accessToken: String,
//    val refreshToken: String
//)
//data class LogoutResponse(
//    val studentId: String,
//    val name: String,
//    val nickname: String,
//    val major: String
//)
//
//data class MakePostResponse(
//    val title:String,
//    val context : String,
//    val studentId: String
//)
//
//data class LoadPostResponse(
//    val currentPage : Int,
//    val totalPages : Int,
//    val totalElment :Int,
//    val boardFindResponseDtoList : ArrayList<BoardFindResponseDtoList>
//
//
//
//
//
//)
//
//data class BoardFindResponseDtoList(
//    val id:Int,
//    val title:String,
//    val price:Int,
//    val content:String,
//    val studentId: String,
//    val createAt:String,
//    val status:Int,
//    val image:List<Image>,
//    val tag:ArrayList<String>,
//    val nickname: String,
//    val major:String
//)
//data class Image(
//    val id:Int,
//    val url : String
//)


