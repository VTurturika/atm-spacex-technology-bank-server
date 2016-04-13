import static spark.Spark.*;

public class BankServer {

    public static void main(String[] args) {

        port(Integer.valueOf(System.getenv("PORT")));

        get("/", (request, response) -> "SpaceX Technology Bank");
    }
}
