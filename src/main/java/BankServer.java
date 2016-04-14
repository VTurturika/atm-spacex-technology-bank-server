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

    private static final String logoHTML = "<html><head><style>body {background: url(\"./spacex-technology.jpg\") " +
                                            "no-repeat; background-size: cover;}</style></head><body></body></html>";
}
