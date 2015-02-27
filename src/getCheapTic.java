import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.junit.WebTester;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

public class getCheapTic {

	private static WebTester tester;

	// Find your Account Sid and Token at twilio.com/user/account
	public static final String ACCOUNT_SID = "AC734f4ca7704ce47d7de3337bdcfd9653";
	public static final String AUTH_TOKEN = "6a7ea5ef1f0b6a883a763a2570c1e776";

	public static void main(String[] args) throws TwilioRestException, FileNotFoundException, UnsupportedEncodingException {

		tester = new WebTester();
		tester.setBaseUrl("http://www.united.com/web/en-US");

		tester.beginAt("/Default.aspx");
		tester.assertTitleEquals("United Airlines - Airline Tickets, Travel Deals and Flights on united.com");
		tester.setTextField("ctl00$ContentInfo$Booking1$Origin$txtOrigin", "was");
		tester.setTextField("ctl00$ContentInfo$Booking1$Destination$txtDestination", "pek");
		tester.setTextField("ctl00$ContentInfo$Booking1$DepDateTime$Depdate$txtDptDate", "12/13/2014");
		tester.setTextField("ctl00$ContentInfo$Booking1$RetDateTime$Retdate$txtRetDate", "1/11/2015");
		tester.clickButton("ctl00_ContentInfo_Booking1_btnSearchFlight");

		tester.assertTitleEquals("Flight Search Results | United Airlines");

		IElement element = tester.getElementById("ctl00_ContentInfo_Results_ShowSegments1_ShowSegment_ctl00_trNonStop");
		List<IElement> e = element.getChildren();
		IElement ele = e.get(0);
		String nonStop = ele.getTextContent(); // Nonstop flights from $1,907

		IElement element2 = tester
				.getElementById("ctl00_ContentInfo_Results_ShowSegments1_ShowSegment_ctl02_trFlightsWithStops");
		List<IElement> e2 = element2.getChildren();
		IElement ele2 = e2.get(0);
		String stops = ele2.getTextContent(); // flights with stops

		String string = nonStop +"; "+ stops;
		
		// compare with history
		// load history
		if(!CompareHistory(string, "ticHistory"))
			SendSMS(string);

		tester.closeBrowser();
		
		
		
		// find southwest ppl
		
//		tester = new WebTester();
//		tester.setBaseUrl("http://www.mitbbs.com");
//
//		tester.beginAt("/mitbbs_bbsbfind.php?board=FleaMarket");
//		tester.setTextField("title", "southwest");
//		tester.setTextField("dt", "1");
//		tester.checkCheckbox("og");
//		tester.submit();
//
//		tester.assertTitleEquals("未名空间(mitbbs.com) - 海外华人第一门户");
//		
//		String result = tester.getElementByXPath("//html//body//center//table[3]//tbody//tr[4]//td//table//tbody//tr[3]//td").getTextContent();
//		
//		if (!result.equals("共找到0篇文章符合条件")) {
//			result = "southwest " + result;
//			if (!CompareHistory(result, "swhistory"))
//				SendSMS(result);
//		}
//		tester.closeBrowser();
		
		// find us airways
		
//		tester = new WebTester();
//		tester.setBaseUrl("http://www.mitbbs.com");
//
//		tester.beginAt("/mitbbs_bbsbfind.php?board=FleaMarket");
//		tester.setTextField("title", "us airways");
//		tester.setTextField("dt", "1");
//		tester.checkCheckbox("og");
//		tester.submit();
//
//		tester.assertTitleEquals("未名空间(mitbbs.com) - 海外华人第一门户");
//		
//		result = tester.getElementByXPath("//html//body//center//table[3]//tbody//tr[4]//td//table//tbody//tr[3]//td").getTextContent();
//		
//		if (!result.equals("共找到0篇文章符合条件")) {
//			result = "us airways " + result;
//			if (!CompareHistory(result, "ushistory"))
//				SendSMS(result);
//		}
//		tester.closeBrowser();
		

	}
	
	private static boolean CompareHistory(String string, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		String content = null;
		   File file = new File(fileName); //for ex foo.txt
		   if(!file.exists()) // no file, build a new one, and save result, return false
		   {
			   PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			   writer.println(string);
			   writer.close();
			   return false;
		   }
		   
		   try {
		       FileReader reader = new FileReader(file);
		       char[] chars = new char[(int) file.length()];
		       reader.read(chars);
		       content = new String(chars);
		       reader.close();
		   } catch (IOException e) {
		       e.printStackTrace();
		   }
		   
		   if(string.equals(content))
			   return true;
		   else {
			   PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			   writer.println(string);
			   writer.close();
			   return false;
		}
		
	}

	public static void SendSMS(String string) throws TwilioRestException
	{
		TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

		// Build a filter for the MessageList
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Body", string));
		params.add(new BasicNameValuePair("To", "+18145808686"));
		params.add(new BasicNameValuePair("From", "+19542288887"));

		MessageFactory messageFactory = client.getAccount().getMessageFactory();
		Message message;

		message = messageFactory.create(params);
		System.out.println(message.getSid());
		
	}
	
	
	

}
