import com.heroku.sdk.jdbc.DatabaseUrl;
import com.sun.org.apache.regexp.internal.RE;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static spark.Spark.*;

public class BankServer {

    public static void main(String[] args) {

        port(Integer.valueOf(System.getenv("PORT")));
        staticFileLocation("/public");
        get("/", (request, response) -> logoHTML);

        //customer requests
        post("/customer/check-pin", (request, response) -> {

            Connection connection = null;

            try {
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = null;

                if(request.queryParams().contains("cardID") && request.queryParams().contains("pinCode") ) {

                    resultSet = statement.executeQuery(String.format("SELECT cardID FROM creditCard WHERE " +
                                                                     "cardID = \'%1$s\' AND pin = \'%2$s\'",
                                                                     request.queryParams("cardID"),
                                                                     request.queryParams("pinCode")));
                    return resultSet.next() ? "OK" : "LOGIN_ERROR";
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return "FATAL_ERROR";
        });
        post("/customer/receive-cash","application/json", (request, response) -> {

            Connection connection = null;
            JSONObject resultJson = new JSONObject().put("Result", "FATAL_ERROR");

            try {
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = null;

                if(request.queryParams().contains("cardID") && request.queryParams().contains("pinCode") &&
                   request.queryParams().contains("cashSize")) {

                    resultSet = statement.executeQuery(String.format("SELECT balance FROM creditCard WHERE " +
                                                                      "cardID = \'%1$s\' AND pin = \'%2$s\'",
                                                                      request.queryParams("cardID"),
                                                                      request.queryParams("pinCode")));
                    if (resultSet.next()) {

                        double currentBalance = Double.parseDouble(resultSet.getString("balance")
                                                                   .substring(1)
                                                                   .replaceAll(",", ""));

                        double requiredAmount = Double.parseDouble(request.queryParams("cashSize"));

                        if(currentBalance >= requiredAmount) {
                            statement.executeUpdate(String.format("UPDATE creditCard SET balance = balance - \'$%1$s\' " +
                                                                  "WHERE cardID = \'%2$s\' AND pin = \'%3$s\'",
                                                                   request.queryParams("cashSize"),
                                                                   request.queryParams("cardID"),
                                                                   request.queryParams("pinCode")));
                            resultSet = statement.executeQuery(String.format("SELECT balance FROM creditCard WHERE " +
                                                                             "cardID = \'%1$s\' AND pin = \'%2$s\'",
                                                                              request.queryParams("cardID"),
                                                                              request.queryParams("pinCode")));
                            if (resultSet.next()) {
                                resultJson.put("Result", "OK").put("Balance", resultSet.getString("balance"));
                                return  resultJson;
                            }
                        }
                        else {
                            return resultJson.put("Result", "INSUFFICIENT_FUNDS");
                        }

                    }
                    else {
                        return resultJson.put("Result", "LOGIN_ERROR");
                    }
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return resultJson;

        });
        post("/customer/add-cash", "application/json", (request, response) -> {

            Connection connection = null;
            JSONObject resultJson = new JSONObject().put("Result", "FATAL_ERROR");

            try {
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = null;

                if(request.queryParams().contains("cardID") && request.queryParams().contains("pinCode") &&
                   request.queryParams().contains("cashSize")) {

                    statement.executeUpdate(String.format("UPDATE creditCard SET balance = balance + \'$%1$s\' " +
                                                          "WHERE cardID = \'%2$s\' AND pin = \'%3$s\'",
                                                           request.queryParams("cashSize"),
                                                           request.queryParams("cardID"),
                                                           request.queryParams("pinCode")));

                    resultSet = statement.executeQuery(String.format("SELECT balance FROM creditCard WHERE " +
                                                                     "cardID = \'%1$s\' AND pin = \'%2$s\'",
                                                                      request.queryParams("cardID"),
                                                                      request.queryParams("pinCode")));
                    if (resultSet.next()) {
                        resultJson.put("Result", "OK").put("Balance", resultSet.getString("balance"));
                        return  resultJson;
                    }
                    else {
                        return resultJson.put("Result", "LOGIN_ERROR");
                    }
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return resultJson;

        });
        post("/customer/get-balance","application/json", (request, response) -> {

            Connection connection = null;
            JSONObject resultJson = new JSONObject().put("Result", "FATAL_ERROR");

            try {
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = null;

                if(request.queryParams().contains("cardID") && request.queryParams().contains("pinCode") ) {

                    resultSet = statement.executeQuery(String.format("SELECT balance FROM creditCard WHERE " +
                                                                     "cardID = \'%1$s\' AND pin = \'%2$s\'",
                                                                      request.queryParams("cardID"),
                                                                      request.queryParams("pinCode")));
                    if (resultSet.next()) {
                        resultJson.put("Result", "OK").put("Balance", resultSet.getString("balance"));
                        return  resultJson;
                    }
                    else {
                        return resultJson.put("Result", "LOGIN_ERROR");
                    }
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return resultJson;
        });
        post("/customer/change-pin", (request, response) -> {

            Connection connection = null;
            try {
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();

                if(request.queryParams().contains("cardID") && request.queryParams().contains("oldPin") &&
                   request.queryParams().contains("newPin") ) {

                    int rowCount = statement.executeUpdate(String.format("UPDATE creditCard SET pin = \'%1$s\' " +
                                                                         "WHERE pin = \'%2$s\' AND cardID = \'%3$s\';",
                                                                         request.queryParams("newPin"),
                                                                         request.queryParams("oldPin"),
                                                                         request.queryParams("cardID")));
                    return (rowCount == 1) ? "OK" : "LOGIN_ERROR";
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return "FATAL_ERROR";
        });

        //service worker requests
        post("/service/test-connection", (request, response) -> "Ready");
        post("/service/check-service-key", (request, response) -> {

            Connection connection = null;
            try {
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = null;

                if(request.queryParams().contains("serviceKey")) {

                    resultSet = statement.executeQuery(String.format("SELECT serviceKey FROM ServiceWorker " +
                                                                     "WHERE serviceKey = \'%1$s\';",
                                                                      request.queryParams("serviceKey")));
                    return resultSet.next() ? "OK" : "LOGIN_ERROR";
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return "FATAL_ERROR";

        });
        post("/service/create-account","application/json", (request, response) -> {

            Connection connection = null;
            JSONObject resultJson = new JSONObject().put("Result", "FATAL_ERROR");
            try {
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = null;

                if( request.queryParams().contains("serviceKey") &&
                    request.queryParams().contains("customerFirstName") &&
                    request.queryParams().contains("customerMiddleName") &&
                    request.queryParams().contains("customerLastName") &&
                    request.queryParams().contains("customerAddress") &&
                    request.queryParams().contains("customerAge") )
                {
                    resultSet = statement.executeQuery(
                            String.format("INSERT INTO BankAccount(customerFirstName, customerMiddleName," +
                                          "customerLastName,customerAge,customerAddress) " +
                                          "VALUES (\'%1$s\',\'%2$s\',\'%3$s\',%4$s,\'%5$s\');" +
                                          "SELECT currval('bankaccount_accountid_seq');",
                                           request.queryParams("customerFirstName"),
                                           request.queryParams("customerMiddleName"),
                                           request.queryParams("customerLastName"),
                                           request.queryParams("customerAge"),
                                           request.queryParams("customerAddress")));

                    if(resultSet.next()) {
                        return resultJson.put("Result", "OK")
                                         .put("AccountID", resultSet.getString("currval"));
                    }
                    else {
                        return resultJson.put("Result", "LOGIN_ERROR");
                    }
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return resultJson;
        });
        post("/service/add-card", (request, response) -> "");
        post("/service/get-blocked-cards", (request, response) -> "");
        post("/service/block-card", (request, response) -> {

            Connection connection = null;
            try {
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();

                if(request.queryParams().contains("cardID")) {

                    int rowCount = statement.executeUpdate(String.format("UPDATE CreditCard SET isLocked = true " +
                                                                         "WHERE cardId = \'%1$s\';",
                                                                         request.queryParams("cardID")));
                    return (rowCount == 1) ? "OK" : "LOGIN_ERROR";
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return "FATAL_ERROR";
        });
        post("/service/unblock-card", (request, response) -> {

            Connection connection = null;
            try {
                connection = DatabaseUrl.extract().getConnection();
                Statement statement = connection.createStatement();

                if(request.queryParams().contains("cardID") && request.queryParams().contains("serviceKey")) {

                    int rowCount = statement.executeUpdate(String.format("UPDATE CreditCard SET isLocked = false " +
                                                                         "WHERE cardId = \'%1$s\' AND EXISTS " +
                                                                         "(SELECT serviceKey FROM ServiceWorker " +
                                                                         "WHERE serviceKey = \'%2$s\');",
                                                                         request.queryParams("cardID"),
                                                                         request.queryParams("serviceKey")));
                    return (rowCount == 1) ? "OK" : "LOGIN_ERROR";
                }
            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                if (connection != null) try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
            return "FATAL_ERROR";
        });

    }

    private static final String logoHTML = "<html><body><img src=\"./spacex-technology.jpg\" " +
                                            "alt=\"spacex-technology\" style=\"width: 100%\"></body></html>";
}
