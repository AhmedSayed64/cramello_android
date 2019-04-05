package net.aldar.cramello.apiHandler;

import net.aldar.cramello.model.Address;
import net.aldar.cramello.model.Basket;
import net.aldar.cramello.model.FirebaseData;
import net.aldar.cramello.model.RegistrationData;
import net.aldar.cramello.model.SocialLink;
import net.aldar.cramello.model.request.BasketLineRequest;
import net.aldar.cramello.model.request.BasketValidationRequest;
import net.aldar.cramello.model.request.ChangePasswordRequest;
import net.aldar.cramello.model.request.CheckoutRequest;
import net.aldar.cramello.model.request.ForgetPwRequest;
import net.aldar.cramello.model.request.LoginRequest;
import net.aldar.cramello.model.request.MinOrder;
import net.aldar.cramello.model.request.ValidateCode;
import net.aldar.cramello.model.request.VerCodeRequest;
import net.aldar.cramello.model.request.VoucherRequest;
import net.aldar.cramello.model.response.Ad;
import net.aldar.cramello.model.response.Branch;
import net.aldar.cramello.model.response.CheckoutResponse;
import net.aldar.cramello.model.response.ContactData;
import net.aldar.cramello.model.response.DefaultResponse;
import net.aldar.cramello.model.response.LoginData;
import net.aldar.cramello.model.response.Notification;
import net.aldar.cramello.model.response.Order;
import net.aldar.cramello.model.response.UserData;
import net.aldar.cramello.model.response.VerCode;
import net.aldar.cramello.model.response.VoucherResponse;
import net.aldar.cramello.model.response.basket.BasketValidation;
import net.aldar.cramello.model.response.governorate.Governorate;
import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.model.response.product.ProductCategory;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface BaseApi {

    @POST("api/v1/validate-registration")
    Call<RegistrationData> validateRegisterData(@Body RegistrationData data);

    @POST("api/v1/resend-code")
    Call<VerCode> requestVerCode(@Body VerCodeRequest phoneData);

    @POST("api/v1/validate-code/{serial}")
    Call<DefaultResponse> validateVerCode(@Path("serial") String serial,
                                          @Body ValidateCode codeData);

    @POST("api/v1/registration")
    Call<RegistrationData> registerNewAccount(@Body RegistrationData data);

    @POST("api/v1/login")
    Call<LoginData> login(@Body LoginRequest loginRequest);

    @GET("api/v1/user/{userId}")
    Call<UserData> getUserData(@Header("Authorization") String authorizationToken,
                               @Path("userId") int userId);

    @PATCH("api/v1/user/{userId}")
    Call<UserData> updateProfile(@Header("Authorization") String authorizationToken,
                                 @Path("userId") int userId,
                                 @Body UserData userData);

    @POST("api/v1/change-password")
    Call<Void> changePassword(@Header("Authorization") String authorizationToken,
                              @Body ChangePasswordRequest changePasswordRequest);

    @POST("api/v1/forgot-password")
    Call<ForgetPwRequest> forgetPw(@Body ForgetPwRequest emailData);

    @GET("api/v1/social-links")
    Call<List<SocialLink>> getSocialLinks(@Header("Authorization") String authorizationToken);

    @GET("api/v1/mobile-links/1")
    Call<Ad> getAdImage(@Header("Authorization") String authorizationToken);

    @GET("api/v1/addresses")
    Call<List<Address>> getAddresses(@Header("Authorization") String authorizationToken);

    @POST("api/v1/addresses")
    Call<Address> createNewAddress(@Header("Authorization") String authorizationToken,
                                   @Body Address newAddress);

    @PATCH("api/v1/addresses/{AddressId}")
    Call<Address> editAddress(@Header("Authorization") String authorizationToken,
                              @Path("AddressId") int addressId,
                              @Body Address editedAddress);

    @DELETE("api/v1/addresses/{AddressId}")
    Call<Void> deleteAddress(@Path("AddressId") int addressId,
                             @Header("Authorization") String authorizationToken);

    @GET("api/v1/governorates")
    Call<List<Governorate>> getGovernorates(@Header("Authorization") String authorizationToken);

    @GET("api/v1/categories")
    Call<List<ProductCategory>> getCategories(@Header("Authorization") String authorizationToken);

    @GET("api/v1/products")
    Call<List<Product>> getProducts(@Header("Authorization") String authorizationToken);

    @GET("api/v1/minimum-order/1")
    Call<MinOrder> getMinOrderValue(@Header("Authorization") String authorizationToken);

    @POST("api/v1/basket")
    Call<Basket> createNewBasket(@Header("Authorization") String authorizationToken,
                                 @Body Basket basket);

    @GET("api/v1/basket")
    Call<List<Basket>> getMyBasket(@Header("Authorization") String authorizationToken,
                                   @Query("user") int userId,
                                   @Query("id") int basketId);

    @PATCH("api/v1/basket/{basketId}")
    Call<Basket> updateBasketInfo(@Header("Authorization") String authorizationToken,
                                  @Path("basketId") int basketId,
                                  @Body Basket basket);

    @POST("api/v1/basket-lines")
    Call<BasketLineRequest> createBasketLine(@Header("Authorization") String authorizationToken,
                                             @Body BasketLineRequest newBasketLine);

    @PATCH("api/v1/basket-lines/{basketLineId}")
    Call<BasketLineRequest> modifyBasketLine(@Header("Authorization") String authorizationToken,
                                             @Path("basketLineId") int basketLineId,
                                             @Body BasketLineRequest newBasketLine);

    @DELETE("api/v1/basket-lines/{basketLineId}")
    Call<Void> deleteBasketLine(@Header("Authorization") String authorizationToken,
                                @Path("basketLineId") int basketLineId);

    @POST("api/v1/validate-basket")
    Call<BasketValidation> validateBasket(@Header("Authorization") String authorizationToken,
                                          @Header("Accept-Language") String acceptLanguage,
                                          @Header("Content-Language") String contentLanguage,
                                          @Body BasketValidationRequest basketValidationRequest);

    @POST("api/v1/validate-voucher")
    Call<VoucherResponse> validateVoucherCode(@Header("Authorization") String authorizationToken,
                                              @Body VoucherRequest voucherCode);

    @POST("api/v1/checkout")
    Call<CheckoutResponse> postCheckout(@Header("Authorization") String authorizationToken,
                                        @Body CheckoutRequest checkoutRequest);

    @GET("api/v1/orders")
    Call<List<Order>> getMyOrders(@Header("Authorization") String authorizationToken,
                                  @Query("transaction__number") String transactionNo,
                                  @Query("user") Integer userId);

    @GET("api/v1/contact-details/1")
    Call<ContactData> getContactData(@Header("Authorization") String authorizationToken);

    @GET("api/v1/branches")
    Call<List<Branch>> getBranches(@Header("Authorization") String authorizationToken);

    @GET("api/v1/notification-history")
    Call<List<Notification>> getNotificationsHistory(@Header("Authorization") String authorizationToken);

    @POST("devices")
    Call<FirebaseData> registerNewToken(@Body FirebaseData firebaseData);


    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
