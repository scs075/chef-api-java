package edu.tongji.wang.chefapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.tongji.wang.chefapi.method.ApiMethod;

public class ChefClientTest extends TestCase {
	
	@Test
	public void testGet(){
		ChefApiClient cac = new ChefApiClient("z082508", "/Users/z082508/Downloads/z082508.pem", "https://jgrlx2036.target.com");

		System.out.println("cac =>" + cac);

		ApiMethod am =cac.get("/organizations/esvprd/search/node?q=chef_environment:production%20AND%20recipes:api-products-v3");
		int code = am.execute().getReturnCode();

		System.out.println("code=" + code);
		assertEquals(200, code);
		try {
			readJsonStream(am.getResponseBodyAsStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void readJsonStream(InputStream in) throws IOException {
		
		assertNotNull(in);

		Reader reader = new InputStreamReader(in);
		Gson gson = new GsonBuilder().create();
		ChefResult p = gson.fromJson(reader, ChefResult.class);
		System.out.println(p);
	}

}
