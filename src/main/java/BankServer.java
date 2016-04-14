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
    }

    private static final String logoHTML = "<html><body><img src=\"./spacex-technology.jpg\" " +
                                            "alt=\"spacex-technology\" style=\"width: 100%\"></body></html>";
}
