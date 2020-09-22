package com.example.ucochat.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_j-rKDU:APA91bEq6cW9jjghDLurhHXPm34Q80RHIwsG5AlOyy5i3ge3HLaxEWZZ70dkUzPa-Ff8GLDWHmHb5pB-vb_dZq9cUeTzMCfifWtDvaFQKwH3zAtAjxCMALaXm6e1kOWiy8sJHf9P5E_s"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body );
}
