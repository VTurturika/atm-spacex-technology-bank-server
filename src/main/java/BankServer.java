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
        post("/customer/check-pin", (request, response) -> "");
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
