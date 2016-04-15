import com.heroku.sdk.jdbc.DatabaseUrl;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static spark.Spark.*;

public class BankServer {

    public static void main(String[] args) {

        try{
            port(Integer.valueOf(System.getenv("PORT")));
        }catch (Exception e) {
            port(80);
        }

        staticFileLocation("/public");
        get("/", (request, response) -> logoHTML);

        //customer requests
        post("/customer/check-pin", (request, response) -> {

            Connection connection = null;

            try{
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = null;

                if(request.queryParams().contains("cardID") && request.queryParams().contains("pinCode") ) {

                    resultSet = statement.executeQuery(
                                String.format("SELECT cardID FROM creditCard WHERE " +
                                              "cardID = \'%1$s\' AND pin = \'%2$s\'",
                                              request.queryParams("cardID"), request.queryParams("pinCode")));
                    return resultSet.next() ? "OK" : "LOGIN_ERROR";
                }
                else return "LOGIN_ERROR";
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return "FATAL_ERROR";
        });
        post("/customer/receive-cash", (request, response) -> "");
        post("/customer/add-cash", (request, response) -> "");
        post("/customer/get-balance", (request, response) -> "");
        post("/customer/change-pin", (request, response) -> "");

        //service worker requests
        post("/service/test-connection", (request, response) -> "Ready");
        post("/service/check-service-key", (request, response) -> "");
        post("/service/create-account", (request, response) -> "");
        post("/service/add-card", (request, response) -> "");
        post("/service/get-blocked-cards", (request, response) -> "");
        post("/service/block-card", (request, response) -> "");
        post("/service/unblock-card", (request, response) -> "");


    }

    private static final String logoHTML = "<html><body><img src=\"./spacex-technology.jpg\" " +
                                            "alt=\"spacex-technology\" style=\"width: 100%\"></body></html>";
}
