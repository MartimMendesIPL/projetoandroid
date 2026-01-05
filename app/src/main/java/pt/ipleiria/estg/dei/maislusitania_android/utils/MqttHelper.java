package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

public class MqttHelper {
    private static final String TAG = "MqttHelper";
    private static MqttHelper instance;
    private MqttClient mqttClient;
    private MqttMessageListener messageListener;
    private Context appContext;

    private static final String BROKER_URL = "tcp://172.22.21.218:8080";
    private static final String CLIENT_ID = "MaisLusitaniaAndroid_";
    private static final String MQTT_USERNAME = "lusitania";
    private static final String MQTT_PASSWORD = "12345678";

    public interface MqttMessageListener {
        void onMessageReceived(String topic, String message);
    }

    private MqttHelper() {}

    public static synchronized MqttHelper getInstance() {
        if (instance == null) {
            instance = new MqttHelper();
        }
        return instance;
    }

    public void setMessageListener(MqttMessageListener listener) {
        this.messageListener = listener;
    }

    public interface MqttConnectionListener {
        void onConnected();
        void onConnectionFailed(String error);
    }

    private MqttConnectionListener connectionListener;

    public void setConnectionListener(MqttConnectionListener listener) {
        this.connectionListener = listener;
    }

    public void connect(Context context) {
        this.appContext = context.getApplicationContext();

        new Thread(() -> {
            try {
                String clientId = CLIENT_ID + System.currentTimeMillis();
                mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());

                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(MQTT_USERNAME);
                options.setPassword(MQTT_PASSWORD.toCharArray());
                options.setCleanSession(true);
                options.setAutomaticReconnect(true);
                options.setConnectionTimeout(10);

                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.e(TAG, "Conexão MQTT perdida: " + (cause != null ? cause.getMessage() : "desconhecido"));
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        String payload = new String(message.getPayload());
                        Log.i(TAG, "Mensagem recebida - Tópico: " + topic + " | Msg: " + payload);

                        try {
                            JSONObject json = new JSONObject(payload);
                            String localNome = json.getString("local_nome");
                            String titulo = json.getString("titulo");

                            String notificationMessage = "Algo está a acontecer no " + localNome +
                                    "! Já há um novo evento disponível chamado " + titulo + ".";

                            NotificationHelper.showEventNotification(
                                    appContext,
                                    "Novo Evento",
                                    notificationMessage
                            );

                        } catch (JSONException e) {
                            Log.e(TAG, "Erro ao fazer parse da mensagem: " + e.getMessage());
                        }

                        if (messageListener != null) {
                            messageListener.onMessageReceived(topic, payload);
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {}
                });

                mqttClient.connect(options);
                Log.i(TAG, "Conectado ao broker MQTT com autenticação");

                if (connectionListener != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                            connectionListener.onConnected()
                    );
                }

            } catch (MqttException e) {
                Log.e(TAG, "Erro ao conectar MQTT: " + e.getMessage());
                if (connectionListener != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                            connectionListener.onConnectionFailed(e.getMessage())
                    );
                }
            }
        }).start();
    }

    public void subscribe(String localNome) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            Log.e(TAG, "Cliente MQTT não conectado");
            return;
        }
        try {
            String topic = "EVENTOS " + localNome;
            mqttClient.subscribe(topic, 1);
            Log.i(TAG, "Subscrito ao tópico: " + topic);
        } catch (MqttException e) {
            Log.e(TAG, "Erro ao subscrever: " + e.getMessage());
        }
    }

    public void unsubscribe(String localNome) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            return;
        }
        try {
            String topic = "EVENTOS " + localNome;
            mqttClient.unsubscribe(topic);
            Log.i(TAG, "Desinscrito do tópico: " + topic);
        } catch (MqttException e) {
            Log.e(TAG, "Erro ao desinscrever: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            Log.e(TAG, "Erro ao desconectar: " + e.getMessage());
        }
    }
}
