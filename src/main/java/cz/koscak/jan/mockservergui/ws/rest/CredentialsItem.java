package cz.koscak.jan.mockservergui.ws.rest;

public class CredentialsItem {
	
	private String url = null;
	private String username = null;
	private String password = null;
	
	public CredentialsItem(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
}
