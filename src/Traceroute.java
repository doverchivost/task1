import java.io.*;
import java.net.*;
import java.lang.*;
import org.json.*;
import java.nio.charset.Charset;

class Traceroute
{
    public static void main(String args[]) throws IOException {
        System.out.print("Write an ip-address or a Domain name: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s = br.readLine();
        System.out.println();

        tablePrint("№", "IP", "AS", "COUNTRY", "PROVIDER");
        BufferedReader in;
        try{
            Runtime r   =   Runtime.getRuntime();
            Process p   =   r.exec("tracert -d " + s);

            in  =   new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;

            for (int i =0; i < 4; i++) in.readLine();
            String[] first = in.readLine().split(" ");
            tablePrint("1", first[first.length - 1], "", "", "");
            int count = 2;
            if(p==null)
                System.out.println("could not connect");

            while((line=in.readLine())!=null){
                if (line.contains("*") || !line.contains("ms")) break;
                String[] trace = line.split(" ");
                String ip = trace[trace.length - 1];
                readJSON(Integer.toString(count), ip);
                count++;
                //in.close();
            }

        }catch(IOException e){

            System.out.println(e.toString());

        }
    }

    public static void readJSON (String n, String ip) throws IOException {
        String url = "http://ip-api.com/json/" + ip;
        JSONObject obj = readJsonFromUrl(url);
        //System.out.println("----" + ip + "---" + readJsonFromUrl(url));
        if (!obj.getString("status").equals("fail")) {
            String as = obj.getString("as");
            String country = obj.getString("country");
            String provider = obj.getString("isp");
            tablePrint(n, ip, as, country, provider);
        }
        else {
            tablePrint(n, ip, "private", "private", "private");
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static void tablePrint (String n, String ip, String as, String country, String provider) {
        System.out.format("%5s%18s%40s%20s%50s", n, ip, as, country, provider);
        System.out.println();
    }
}