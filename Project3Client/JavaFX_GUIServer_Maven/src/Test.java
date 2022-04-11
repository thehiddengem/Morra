import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;



class MorraTest {
	Client client1;
	Integer Port;
	boolean  P1WantsReplay, P2WantsReplay;
	String IP;	

	@BeforeEach
	void init() {
		client1 = new Client();
	}
	
	@Test
	void IPChecker() {
		IP = "127.0.0.1";
		
		client1.createConnection(IP);
		
		assertEquals(IP, client1.getIP(), "IP was not properly given to client");	
	}
	@Test
	void PortChecker() {
		Port = 5556;
		
		client1.setPort(Port);
		
		assertEquals(Port, client1.getPort(), "Port was not properly given to client");	
	}
	
	@Test
	void PortChecker() {
		Port = 555655;
		
		client1.setPort(Port);
		
		assertEquals(Port, client1.getPort(), "Port was not properly given to client");	
	}
	
	@Test
	void validPortChecker() {
		Port = 100000;
		client1.setPort(Port);
		
		assertFalse(client1.validPortNumber());
	}
	@Test
	void testWhoWon1() {
		client.clientInfo.setP1Points(2);
		client.clientInfo.setP2Points(0);

		assertEquals(1, client.whoWon());
	}
	