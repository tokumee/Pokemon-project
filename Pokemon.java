package box.of.laplace.ex.java.data_engineering;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.json.JSONArray;
import org.json.JSONObject;

public class Pokemon{
public static void main(String[] args) {
	try {
		//Issのデータを取得
		URL ISSurl = new URL("http://api.open-notify.org/iss-now.json");
		HttpURLConnection ISSconnection = (HttpURLConnection) ISSurl.openConnection();
		ISSconnection.setRequestMethod("GET");
		
		int Intkeido=0,Intido=0,Inttime=0,Fourth=0,Fifth=0,Sixth=0;
		if(ISSconnection.getResponseCode()==200) {
			
             JSONObject ISSjson = new JSONObject(getResponseContent(ISSconnection));
             double keido = Math.abs(ISSjson.getJSONObject("iss_position").getDouble("latitude"));
             double ido = Math.abs(ISSjson.getJSONObject("iss_position").getDouble("longitude"));
             while(true) {
            	Random random = new Random();
            	int G = random.nextInt(30,40);
            	 
                Intkeido = (int)keido;
                Intido = (int)ido;
                Inttime = ISSjson.getInt("timestamp")% 1000;
                Fourth = (Intkeido + Intido + Inttime);
                Fifth = Inttime/(1+Intkeido*(int)Math.cos(Intido));//ケプラーの法則　r=p/(1+εcosΘ)
                Sixth = (G*Intkeido*Inttime)/Fifth^2;  //万有引力の法則　F=(Gm1m2)/r^2
                
                
                //System.out.println("aiueo");		//動作確認用プログラム
                
                if(Inttime < 0) Inttime = Inttime * -1;
                if(Fourth < 0) Fourth = Fourth * -1;
                if(Fifth < 0) Fifth = Fifth * -1;
                if(Sixth < 0) Sixth = Sixth * -1;
                
                if(Intkeido < 1026) Intkeido = Intkeido /2;
                if(Intido < 1026) Intido = Intido /3;
                if(Inttime < 1026) Inttime = Inttime -33;
                if(Fourth < 1026) Fourth = Fourth - Intkeido;
                if(Fifth < 1026) Fifth = Fifth / 2;
                if(Sixth < 1026) Sixth = Sixth - Intido;
                
                if(Inttime < 1026 && Fourth < 1026 && Fifth < 1026 && Sixth < 1026) break;	
			}
			System.out.println(Intkeido + " " + Intido + " " + Inttime + " " + Fourth + " " + Fifth + " " + Sixth + " ");
		}else {
			ISSconnection.disconnect();
		}
		PokemonMoves(Intkeido);
		PokemonMoves(Intido);
		PokemonMoves(Inttime);
		PokemonMoves(Fourth);
		PokemonMoves(Fifth);
		PokemonMoves(Sixth);
		
		PokemonPicture(Intkeido,Intido,Inttime,Fourth,Fifth,Sixth);
	}catch(Exception e) {
		e.printStackTrace();
		
	}
}
private static String getResponseContent(HttpURLConnection connection) throws Exception {
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder content = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
    }
    in.close();
    return content.toString();
}

private static void PokemonMoves(int id) throws Exception {
	URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + id + "/");
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setRequestMethod("GET");
	if(connection.getResponseCode() == 200) {
		JSONObject PKjson = new JSONObject(getResponseContent(connection));
		String name = PKjson.getString("name");
		
		System.out.println("\nPokemon's ID :" + id +"\nPokemon's Name :" + name);
		
		JSONArray movesArray = PKjson.getJSONArray("moves");
		List<Move> moves = new ArrayList<>();
		
        for (int i = 0; i < movesArray.length(); i++) {
            String moveUrl = movesArray.getJSONObject(i).getJSONObject("move").getString("url");
            HttpURLConnection moveConnection = (HttpURLConnection) new URL(moveUrl).openConnection();
            moveConnection.setRequestMethod("GET");

            if (moveConnection.getResponseCode() == 200) {
                JSONObject moveData = new JSONObject(getResponseContent(moveConnection));
                String moveName = moveData.getString("name");
                int power = moveData.optInt("power", -1); // 威力がない場合は-1
                if (power != -1) {
                    moves.add(new Move(moveName, power));
                }
            }
            moveConnection.disconnect();
        }
        //技のソート
        for (int i = 0; i < moves.size() - 1; i++) {
            for (int j = 0; j < moves.size() - 1 - i; j++) {
                if (moves.get(j).getPower() < moves.get(j + 1).getPower()) {
                    // 威力が小さい場合、隣の要素と入れ替える
                    Move temp = moves.get(j);
                    moves.set(j, moves.get(j + 1));
                    moves.set(j + 1, temp);
                }
            }
        }
        System.out.println("Top moves :");
        for(int i=0;i < Math.min(4, moves.size()); i++) {
        	Move move = moves.get(i);
        	System.out.println(move.name + " - Power: " + move.power);
        }
		connection.disconnect();
	}
}
private static void PokemonPicture(int id1,int id2,int id3,int id4,int id5,int id6) {
	try {
        URL[] urls = {
                new URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id1 + ".png"),
                new URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id2 + ".png"),
                new URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id3 + ".png"),
                new URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id4 + ".png"),
                new URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id5 + ".png"),
                new URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id6 + ".png")
            };
        BufferedImage[] images = new BufferedImage[urls.length];
        for (int i = 0; i < urls.length; i++) {
            images[i] = ImageIO.read(urls[i]);
        }
        
        JFrame frame = new JFrame("Battle Pokemon's");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2,3));
        frame.setSize(800,800);
        
        for (BufferedImage image : images) {
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                }
            };
            frame.add(panel);
        }
        frame.setVisible(true);
		
	} catch (MalformedURLException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	} catch (IOException e) {
		// TODO 自動生成された catch ブロック
		e.printStackTrace();
	}
	
}

private static class Move{
	String name;
	int power;
	
	public Move(String name,int power) {
		this.name = name;
		this.power = power;
	}
	public int getPower() {
		return power;
	}
}
}