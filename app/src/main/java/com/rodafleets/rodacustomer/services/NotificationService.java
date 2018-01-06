package com.rodafleets.rodacustomer.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rodafleets.rodacustomer.R;
import com.rodafleets.rodacustomer.RodaDriverApplication;
import com.rodafleets.rodacustomer.VehicleRequestActivity;
import com.rodafleets.rodacustomer.utils.AppConstants;
import com.rodafleets.rodacustomer.utils.ApplicationSettings;

import org.json.JSONObject;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = AppConstants.APP_NAME;

    /**
     * Called when message is received.
     *
     * @param @remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onCreate() {
        Log.i(TAG, "Service Created");

        RodaDriverApplication.vehicleRequestService = this;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        String notificationTitle = "";
        String notificationBody = "";
        Map<String, String> data = null;
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            data = remoteMessage.getData();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        Intent intent = null;
        if (notificationTitle.equals("Request")) {
            intent = new Intent("Vehicle_Requested");
            JSONObject vehicleRequest = new JSONObject(data);
            ApplicationSettings.setVehicleRequest(this, vehicleRequest);
        } else if (notificationTitle.equals("Accept")) {
            intent = new Intent("Bid_Accepted");
        } else if (notificationTitle.equals("Request_Accepted")) {
            //This is for customer side
            System.out.println("Vehicle request has been accepted!");
            intent = new Intent("Request_Accepted");
            if (remoteMessage.getData().size() > 0) {
                Map<String, String> dataMessage = remoteMessage.getData();
                //driver details
                intent.putExtra("driverId",dataMessage.get("driverId"));
                intent.putExtra("driverName", dataMessage.get("driverName"));
                intent.putExtra("driverContact", dataMessage.get("driverContact"));
                intent.putExtra("driverRating",dataMessage.get("driverRating"));
                intent.putExtra("driverDistance",dataMessage.get("driverDistance"));
                //vehicle details
                intent.putExtra("vehicleRegId", dataMessage.get("vehicleRegId"));
                //reqeust id
                intent.putExtra("requestId", dataMessage.get("requestId"));
                intent.putExtra("bid", dataMessage.get("bid"));
                intent.putExtra("Amount", dataMessage.get("Amount"));
            }

        } else if (notificationTitle.equals("Driver_Location_Updated")) {
            if (remoteMessage.getData().size() > 0) {
                Map<String, String> dataMessage = remoteMessage.getData();
                final String reqId = dataMessage.get("reqId");
                System.out.println("Driver location is updated for reqId " + reqId);
                intent = new Intent("Location_Updated_" + reqId);
                intent.putExtra("driverId", dataMessage.get("driverId"));
                intent.putExtra("bId", dataMessage.get("driverId"));
                intent.putExtra("lat", dataMessage.get("lat"));
                intent.putExtra("lan", dataMessage.get("lan"));
            }
        } else if (notificationTitle.equals("TRIP_UPDATED")) {
            if (remoteMessage.getData().size() > 0) {
                Map<String, String> dataMessage = remoteMessage.getData();
                final String driverId = dataMessage.get("driverId");
                final String tripId = dataMessage.get("currentTripId");
                final String bId = dataMessage.get("bId");
                final String reqId = dataMessage.get("reqId");
                final String status = dataMessage.get("status");
                int tripStatus = Integer.parseInt(status);
                String notificationBodyMessage = "Trip Status is ";
                switch (tripStatus) {
                    case 0: {
                        notificationBodyMessage = notificationBodyMessage + "Confirmed";
                        break;
                    }
                    case 1: {
                        notificationBodyMessage = notificationBodyMessage + "Loading";
                        break;
                    }
                    case 2: {
                        notificationBodyMessage = notificationBodyMessage + "Started";
                        break;
                    }
                    case 3: {
                        notificationBodyMessage = notificationBodyMessage + "Unloading";
                        break;
                    }
                    case 10: {
                        notificationBodyMessage = notificationBodyMessage + "Completed";
                        break;
                    }
                }
                sendNotification("Trip Status Updated", notificationBodyMessage);
            }
        }
        if (null != intent) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, VehicleRequestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("FROM_NOTIFICATION", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}