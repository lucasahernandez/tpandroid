package com.tdam.tpa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;


public class ServidorConexion

{
	protected ArrayList<MensajeWebEntidad> messages;
	protected SimpleDateFormat formater;
	protected boolean sucess = false;

	public ServidorConexion() {
		formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	}


	public boolean sendMessage(MensajeWebEntidad message) {

		StringBuilder query = new StringBuilder();
		query.append("<action id=\"REQUEST_RANDOM_VALUE\" name=\"send-message\">"
				+ "<action-detail><auth username=\"");
		query.append(UsuarioEntidad.name);
		query.append("\" key=\"");
		query.append(UsuarioEntidad.password);
		query.append("\"></auth><message to=\"");
		query.append(message.user);
		query.append("\"><![CDATA[");
		query.append(message.message);
		query.append("]]></message></action-detail></action>");
		sendQuery(query.toString());
		return sucess;
	}

	public ArrayList<MensajeWebEntidad> getMessages(Date lastTimeStamp) {
		StringBuilder query = new StringBuilder();
		messages = new ArrayList<MensajeWebEntidad>();
		query.append("<action id=\"REQUEST_RANDOM_VALUE\" name=\"get-messages\">"
				+ "<action-detail><auth username=\"");
		query.append(UsuarioEntidad.name);
		query.append("\" key=\"");
		query.append(UsuarioEntidad.password);
		query.append("\"></auth><filter type=\"timestamp\">");
		query.append(formater.format(lastTimeStamp));
		query.append("</filter></action-detail></action>");
		sendQuery(query.toString());
		return messages;

	}

	public boolean registerUser(String user, String password) {
		StringBuilder query = new StringBuilder();
		query.append("<action id=\"REQUEST_RANDOM_VALUE\" name=\"register-user\">"
				+ "<action-detail><user username=\"");
		query.append(user);
		query.append("\" password=\"");
		query.append(password);
		query.append("\"/></action-detail></action>");
		sendQuery(query.toString());
		return sucess;
	}
	public boolean isConnectedToServer() {
	    try{
	        URL myUrl = new URL("http://192.168.0.10:8081/MessageSender/");
	        URLConnection connection = myUrl.openConnection();
	        connection.setConnectTimeout(5);
	        connection.connect();
	        return true;
	    } catch (Exception e) {
	        // Handle your exceptions
	        return false;
	    }
	}

	private void sendQuery(String query) {
		StringBuilder response = new StringBuilder();
		try {
			URL serverAdress = new URL("http://192.168.0.10:8081/MessageSender/");
			URLConnection serverConnection = serverAdress.openConnection();

			serverConnection.setDoOutput(true);
			if (serverConnection instanceof HttpURLConnection) {
				((HttpURLConnection) serverConnection).setRequestMethod("POST");
			}

			OutputStreamWriter serverOutput = new OutputStreamWriter(
					serverConnection.getOutputStream());

			serverOutput.write(query);

			serverOutput.close();

			BufferedReader serverInput = new BufferedReader(
					new InputStreamReader(serverConnection.getInputStream()));

			String s = "";
			while ((s = serverInput.readLine()) != null) {
				response.append(s);
			}

			InputSource textXml = new InputSource();
			textXml.setCharacterStream(new StringReader(response.toString()));

			XmlParser parser = new XmlParser();
			parser.initParser(textXml);
			serverInput.close();

		} catch (Exception e) {
			 /*messages.add(new WebMailInfo("maria",e.getClass().getName(),new
			 Date(),true));*/
		}
		// return response.toString();
	}

	class XmlParser extends DefaultHandler {
		private Stack<String> nodes;
		private MensajeWebEntidad tempMessage;

		public XmlParser() {
			nodes = new Stack<String>();

		}

		public boolean initParser(InputSource source) {
			boolean result = true;
			SAXParserFactory factory = SAXParserFactory.newInstance();

			try {

				SAXParser builder = factory.newSAXParser();
				builder.parse(source, this);

			} catch (ParserConfigurationException e) {
				result = false;
			} catch (SAXException e) {
				result = false;
			} catch (IOException e) {
				result = false;
			}

			// messages.add(new WebMailInfo("marian",message,new Date(),true));
			return result;
		}

		@Override
		public void startElement(String uri, String nombreLocal,
				String qNombre, Attributes attributes) {
			nodes.push(qNombre);
			if (qNombre.compareTo("message") == 0) {

				tempMessage = new MensajeWebEntidad();
				tempMessage.user = attributes.getValue("from");
				try {
					tempMessage.timeStamp = formater.parse(attributes
							.getValue("timestamp"));
				} catch (ParseException e) {
					tempMessage.message = e.getMessage();
					tempMessage.timeStamp = new Date();
				}
				tempMessage.direction = true;
			}
			if (qNombre.compareTo("result") == 0) {
				String resultType = attributes.getValue("type");
				sucess = (resultType.compareTo("success") == 0) ? true : false;
			}
		}

		@Override
		public void characters(char[] characters, int begin, int length) {
			if (nodes.peek().compareTo("message") == 0) {
				tempMessage.message = new String(characters, begin, length);
				messages.add(tempMessage);
			}
		}

		@Override
		public void endElement(String espacioNombreURI, String nombreLocal,
				String qNombre) {
			nodes.pop();
		}
	}
}