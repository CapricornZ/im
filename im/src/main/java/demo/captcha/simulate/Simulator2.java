package demo.captcha.simulate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.core.io.ClassPathResource;

public class Simulator2 {
	
	private Random random = new Random();
	
	private String repository;
	private List<Item> repo = new ArrayList<Item>();
	public void setRepository(String repo) throws IOException{
		
		this.repository = repo;
		BufferedReader br = null;
		if(this.repository.startsWith("classpath:")){
			
			String file = this.repository.substring("classpath:".length());
			InputStream in = new ClassPathResource(file).getInputStream();
			br = new BufferedReader(new java.io.InputStreamReader(in));
		} else 
			br = new BufferedReader(new FileReader(this.repository));
		
		String line="";
		StringBuffer buffer = new StringBuffer();
		while((line = br.readLine())!=null){
			buffer.append(line);
		}
		String fileContent = buffer.toString();
		
		String[][] json = new com.google.gson.Gson().fromJson(fileContent, String[][].class);
		for(int i=0; i<json.length; i++){
			this.repo.add(new Item(json[i]));
		}
	}
	
	public CaptchaReq generateCaptcha() throws IOException{
		
		Item item = this.repo.get(this.random.nextInt(this.repo.size()));
		return item.generate();
	}
}
