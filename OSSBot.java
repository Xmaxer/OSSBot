package scripts.ossbot;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.Thread.State;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.Screenshots;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.script.Script;
import org.tribot.script.interfaces.MessageListening07;

public class OSSBot extends Script implements MessageListening07{
	private Path relpath=Paths.get("");
	private String MainPath=relpath.toAbsolutePath().toString();
	private File file;
	//private SimpleDateFormat date=new SimpleDateFormat("MMM d, YYYY");
	//private SimpleDateFormat time=new SimpleDateFormat("HH:mm:ss");

	//private SimpleDateFormat fullFormat=new SimpleDateFormat("MMM d HH:mm:ss z");
	//private SimpleDateFormat fileFormat=new SimpleDateFormat("MMM d HH-mm-ss z");
	private List<String> parts=new ArrayList<>();
	private List<String> parts2=new ArrayList<>();
	private ArrayList<String> list=new ArrayList<String>();
	private boolean found=false;
	private String kickMessage=null;
	private ArrayList<String> commandIssuer=new ArrayList<String>();
	private ArrayList<String> command=new ArrayList<String>();
	private ArrayList<String> commandIssuerPM=new ArrayList<String>();
	private ArrayList<String> commandPM=new ArrayList<String>();
	private ArrayList<String> votes=new ArrayList<String>();
	private ArrayList<String> voters=new ArrayList<String>();
	private long lastBadRank = 0;
	private long runTime = 0;
	private boolean recordChat = true;
	//private static LinkedHashMap<String,ArrayList<String>> eventMap=new LinkedHashMap<String,ArrayList<String>>();
	private boolean pollRunning=false;
	private File logging=new File(MainPath+"/Bot_Logs/","logs.txt");
	private String[] ballAnswers={"Signs point to yes.",
			"Yes.",
			"Reply hazy, try again.",
			"Without a doubt.",
			"My sources say no.",
			"As I see it, yes.",
			"You may rely on it.",
			"Concentrate and ask again.",
			"Outlook not so good.",
			"It is decidedly so.",
			"Better not tell you now.",
			"Very doubtful.",
			"Yes - definitely.",
			"It is certain.",
			"Cannot predict now.",
			"Most likely.",
			"Ask again later.",
			"My reply is no.",
			"Outlook good.",
			"Don't count on it.",
			"Yes, in due time.",
			"Definitely not.",
			"You will have to wait.",
			"I have my doubts.",
			"Outlook so so.",
			"Looks good to me!",
			"Who knows?",
			"Looking good!",
			"Probably.",
			"Are you kidding?",
			"Go for it!",
			"Don't bet on it.",
			"Forget about it.",
			"Not since the accident...",
			"Fuck yes.",
			"What would xmax say?",
			"Fuck no.",
	"Mhmm."};
	private String[] congratsMessages={"Congratulations on ",
			"!",
			"Wow, you've achieved ",
			", your mother would be proud!",
			"Only ",
			"? Eh, good job anyway.",
			"Wow, you've achieved ",
			", your father would be proud!",
			"Very nice on getting ",
			"!",
			"",
			", congratz now get 99",
			"",
			", still better than Netty",
			"",
			" gr8 lvl up m8 8/8",
			"Congratulations on ",
			" you filthy casual",
			"",
			"? How was the the tutorial island",
			"Congratulations, you just advanced a ",
	" level. Noob"};
	private String[] jokes={"How do you know when you are going to drown in milk? When its past your eyes!",
			"Milk is also the fastest liquid on earth – its pasteurized before you even see it",
			"A steak pun is a rare medium well done.",
			"Did you hear that the police have a warrant out on a midget psychic ripping people off? It reads \"Small medium at large\"",
			"A panda walks into a bar and says to the bartender \"I’ll have a Scotch and....Coke thank you\". \"Sure thing\" the bartender replies and asks \"but what’s with the big pause?\" ",
			"The panda holds up his hands and says \"I was born with them\"",
			"A man was caught stealing in a supermarket today while balanced on the shoulders of a couple of vampires. He was charged with shoplifting on two counts. ",
			"I heard there was a new store called Moderation. They have everything there",
			"Our wedding was so beautiful, even the cake was in tiers.",
			"Did you hear about the new restaurant on the moon? The food is great, but there’s just no atmosphere.",
			"I went to a book store and asked the saleswoman where the Self Help section was, she said if she told me it would defeat the purpose.",
			"What did the mountain climber name his son? Cliff.",
			"I was thinking about moving to Moscow but there is no point Russian into things.",
			"My New Years resolution is to stop leaving things so late.",
			"If you’re struggling to think of what to get someone for Christmas. Get them a fridge and watch their face light up when they open it.",
			"\"What’s ET short for? Because he’s only got little legs.\"",
			"People are making apocalypse jokes like there’s no tomorrow.",
			"Why do crabs never give to charity? Because they’re shellfish.",
			"What do you call an Argentinian with a rubber toe? Roberto",
			"What do you call a Mexican man leaving the hospital? Manuel",
			"I cut my finger chopping cheese, but I think that I may have grater problems.",
			"Today a girl said she recognized me from vegetarian club, but I’m sure I’ve never met herbivore.",
			"I went to the doctor today and he told me I had type A blood but it was a type O.",
			"When you have a bladder infection, urine trouble.",
			"I dreamed about drowning in an ocean made out of orange soda last night. It took me a while to work out it was just a Fanta sea.",
			"Without geometry life is pointless.",
			"A termite walks into a bar and asks \"Is the bar tender here?\"",
			"What’s Forest Gump’s Facebook password? 1forest1",
			"I gave all my dead batteries away today… Free of charge.",
			"I needed a password eight characters long so I picked Snow White and the Seven Dwarfs.",
			"I am terrified of elevators. I’m going to start taking steps to avoid them.",
			"Tea is for mugs.",
			"What’s the advantage of living in Switzerland? Well, the flag is a big plus.",
			"Why did the octopus beat the shark in a fight? Because it was well armed.",
			"A red and a blue ship have just collided in the Caribbean. Apparently the survivors are marooned.",
			"I’ve deleted the phone numbers of all the Germans I know from my mobile phone. Now it’s Hans free.",
			"Last night me and my girlfriend watched three DVDs back to back. Luckily I was the one facing the TV.",
			"How do you organize a space party? You planet.",
			"How much does a hipster weigh? An instagram.",
			"What do you call a group of killer whales playing instruments? An Orca-stra.",
			"Why was the big cat disqualified from the race? Because it was a cheetah.",
			"A man walked in to a bar with some asphalt on his arm. He said \"Two beers please, one for me and one for the road.\"",
			"Just watched a documentary about beavers… It was the best damn program I’ve ever seen.",
			"Breaking news! Energizer Bunny arrested – charged with battery.",
			"Conjunctivitis.com – now that’s a site for sore eyes.",
			"A Sandwich walks into a bar, the bartender says \"Sorry, we don’t serve food here\"",
			"\"Doctor, I’ve broken my arm in several places\" Doctor \"Well don’t go to those places.\"",
			"I’m on a whiskey diet. I’ve lost three days already.",
			"I fear for the calendar, it’s days are numbered.",
			"There’s a new type of broom out, it’s sweeping the nation.",
			"Atheism is a non-prophet organisation.",
			"Slept like a log last night … woke up in the fireplace.",
			"What did the fish say when it swam into a wall? Damn!",
			"They laughed when I said I wanted to be a comedian – they’re not laughing now.",
			"What cheese can never be yours? Nacho cheese.",
			"A police officer caught two kids playing with a firework and a car battery. He charged one and let the other one off.",
			"Velcro… What a rip-off.",
			"I’m reading a book on the history of glue – can’t put it down.",
			"Where does Napoleon keep his armies? In his sleevies.",
			"I went to the zoo the other day, there was only one dog in it. It was a shitzu.",
			"Why can’t you hear a pterodactyl go to the bathroom? The p is silent.",
			"Q: What’s 50 Cent’s name in Zimbabwe? A: 400 Million Dollars.",
			"\"My Dog has no nose.\" \"How does he smell?\" \"Awful\"",
			"What do you call a cow with no legs? Ground beef.",
			"What did the Buffalo say to his little boy when he dropped him off at school? Bison.",
			"So a duck walks into a pharmacy and says \"Give me some chap-stick… and put it on my bill\"",
			"Why did the scarecrow win an award? Because he was outstanding in his field.",
			"Why did the girl smear peanut butter on the road? To go with the traffic jam.",
			"Why does a chicken coop only have two doors? Because if it had four doors it would be a chicken sedan.",
			"Why don’t seagulls fly over the bay? Because then they’d be bay-gulls!",
			"What do you call a fly without wings? A walk.",
			"What do you do when a blonde throws a grenade at you? Pull the pin and throw it back.",
			"What’s brown and sounds like a bell? Dung!",
			"How do you make a hankie dance? Put a little boogie in it.",
			"Where does batman go to the bathroom? The batroom.",
			"What’s the difference between an African elephant and an Indian elephant? About 5000 miles.",
			"Two muffins were sitting in an oven, and the first looks over to the second, and says, \"man, it’s really hot in here\". The second looks over at the first with a surprised look, and answers, \"WHOA, a talking muffin!\"",
			"A man walks into a bar and orders helicopter flavor chips. The barman replies \"sorry mate we only do plain\"",
			"Sgt.: Commissar! Commissar! The troops are revolting! Commissar: Well, you’re pretty repulsive yourself.",
			"What do you call a sheep with no legs? A cloud.",
			"I knew i shouldn’t have ate that seafood. Because now i’m feeling a little… Eel",
			"What did the late tomato say to the early tomato? I’ll ketch up",
			"What did the 0 say to the 8? Nice belt.",
			"Why didn’t the skeleton cross the road? Because he had no guts.",
			"Why don’t skeletons ever go trick or treating? Because they have nobody to go with.",
			"Why do scuba divers fall backwards into the water? Because if they fell forwards they’d still be in the boat.",
			"Have you ever heard of a music group called Cellophane? They mostly wrap.",
			"What kind of magic do cows believe in? MOODOO.",
			"Wife: Honey I’m pregnant. Me: Well…. what do we do now? Wife: Well, I guess we should go to a baby doctor. Me: Hm.. I think I’d be a lot more comfortable going to an adult doctor.",
			"At what time does the soldier go to the dentist? 1430.",
			"\"Hold on, I have something in my shoe\" \"I’m pretty sure it’s a foot\"",
			"Why does it take longer to get from 1st to 2nd base, than it does to get from 2nd to 3rd base? Because there’s a Shortstop in between!",
			"Dad I’m hungry’ … ‘Hi hungry I’m dad",
			"When phone ringing Dad says ‘If it’s for me don’t answer it.",
			"‘Put the cat out’ … ‘I didn’t realize it was on fire",
			"Where’s the bin? Dad: I haven’t been anywhere!",
			"Can I watch the TV? Dad: Yes, but don’t turn it on.",
			"When Dad drops a pea off of his plate ‘oh dear I’ve pee’d on the table!",
			"Your sex life.",
			"I gave my dead batteries away the other day - free of charge.",
			"French national football team.",
			"osrs servers.",
			"What's best to use against Dust Devils? A vacuum cleaner.",
			"There's a fine line between a numerator and a denominator. But only a fraction of people understand this.",
			"The running isn't that big of a waster.",
			"What's the difference between Tiger Woods and Santa? Santa stopped at 3 ho's",
			"chz's Fire Cape",
			"In queso emergy, what number do you call? 9 Juan Juan.."
	};
	//private static LinkedHashMap<String,Stri> eventDetailsMap=new LinkedHashMap<String,String>();
	private boolean taskComplete=true;
	private long runtime=System.currentTimeMillis();
	private boolean pauseLoginData=false;
	@Override
	public void run() {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Dublin"));
		General.println(System.currentTimeMillis());
		this.setLoginBotState(false);
		setAIAntibanState(false);
		try {
			logging.createNewFile();
		} catch (IOException e) {
			println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }
		}
		/*Thread Reminder=new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					if(taskComplete)
					{
						if(!eventMap.isEmpty())
						{
							for(Entry<String, ArrayList<String>> temp:eventMap.entrySet())
							{
								ArrayList<String> detail=temp.getValue();
								String eventName=temp.getKey();
								if(((Long.parseLong(detail.get(3))+(Long.parseLong(detail.get(1))*60000)) <= System.currentTimeMillis()+900000) && Boolean.valueOf(detail.get(4))==false)
								{
									taskComplete=false;
									detail.set(4, "true");
									eventMap.replace(temp.getKey(), detail);
									printMessage(eventName+ " by "+detail.get(0)+" happening in "+(((Long.parseLong(detail.get(3))+(Long.parseLong(detail.get(1))*60000))-System.currentTimeMillis())/60000)+" mins : "+detail.get(2),true);
									taskComplete=true;
								}
								if((Long.parseLong(detail.get(3))+(Long.parseLong(detail.get(1))*60000)) <= System.currentTimeMillis())
								{
									printMessage(eventName +" by "+detail.get(0)+" about to start : "+detail.get(2),true);
									eventMap.remove(temp.getKey());
								}
							}
						}
					}
					General.sleep(1000);
				}
			}

		});*/
		Thread extractNames=new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{

					list.clear();
					try {
						URL link=new URL("https://docs.google.com/spreadsheets/d/1EKvjECjTnwwT9uJzMSu13UN3sYCF-yQaFFyFXUabZhg/pub?gid=298604662&single=true&output=csv");
						//Code to download
						InputStream in = new BufferedInputStream(link.openStream());
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int n = 0;
						while (-1!=(n=in.read(buf)))
						{
							out.write(buf, 0, n);
						}
						out.close();
						in.close();
						byte[] response = out.toByteArray();
						FileOutputStream fos = new FileOutputStream(MainPath+"/PlayerInfo.csv");
						fos.write(response);
						fos.close();
					} catch (IOException e) {
						println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

					}   
					Scanner scanner;
					try {
						scanner = new Scanner(new File("PlayerInfo.csv"));
						scanner.useDelimiter("[\n,]");
						while (scanner.hasNextLine()) {
							String line = scanner.nextLine();

							Scanner lineScanner = new Scanner(line);
							lineScanner.useDelimiter(",");
							while (lineScanner.hasNext()) {
								String token = lineScanner.next();
								// do whatever needs to be done with token
								list.add(token);
							}
							lineScanner.close();
							// you're at the end of the line here. Do what you have to do.
						}
						scanner.close();
					} catch (FileNotFoundException e) {
						println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

					}

					try {
						/*URL link=new URL("https://docs.google.com/spreadsheets/d/1uBc98kYb_yioYGhHLyDxcagpa7aoPcJM-5usD4_p5TY/pub?gid=875925874&single=true&output=csv");
						//Code to download
						InputStream in = new BufferedInputStream(link.openStream());
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int n = 0;
						while (-1!=(n=in.read(buf)))
						{
							out.write(buf, 0, n);
						}
						out.close();
						in.close();
						byte[] response = out.toByteArray();
						FileOutputStream fos = new FileOutputStream(MainPath+"/Top3temp.csv");
						//println(response.length);
						//println(response.toString());
						fos.write(response);

						//println("test");
						fos.close();
						Scanner scan3=new Scanner(new File("Top3temp.csv"));
						if(scan3.hasNextLine())
						{
							FileOutputStream fos2 = new FileOutputStream(MainPath+"/Top3.csv");
							fos2.write(response);
							fos2.close();
						}
						scan3.close();
						 */
						Scanner cID = new Scanner(new File("CurrentID.txt"));
						int compID = Integer.valueOf(cID.nextLine());
						cID.close();
						URL url = new URL("http://crystalmathlabs.com/tracker/api.php?type=comprankings&competition="+compID);
						URLConnection con = url.openConnection();
						HttpURLConnection http = (HttpURLConnection) con;
						http.setRequestMethod("GET");
						http.setDoOutput(true);
						http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
						http.connect();
						Reader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
						StringBuilder sb = new StringBuilder();
						for (int c; (c = in.read()) >= 0;)
							sb.append((char)c);
						String response = sb.toString();
						if(response.length() >= 30)
						{
							String[] ar = response.split("[,\\s]");
							File topFile = new File(MainPath+"/Top_comp.txt");
							FileWriter fw1 = new FileWriter(topFile,false);
							fw1.write("");
							fw1.close();
							FileWriter fw = new FileWriter(topFile,true);
							for(String s: ar)
							{
								fw.write(s + System.lineSeparator());
							}
							fw.close();
						}
					} catch (Exception e) {
						println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

					}   
					General.sleep(20000);
				}
			}

		});
		Thread kickListener=new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{
					if(kickMessage != null)
					{
						if(kickMessage.toLowerCase().contains("Attempting to kick player from friends chat...".toLowerCase()))
						{
							println("Take screenshot");
							SimpleDateFormat fileFormat=new SimpleDateFormat("MMM d HH-mm-ss z");
							fileFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
							Screenshots.take(fileFormat.format(System.currentTimeMillis())+".png", false, false);
							kickMessage=null;
						}
					}
					General.sleep(100);
				}
			}

		});
		Thread nameListener=new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{
					try{
						File folder=new File(MainPath+"/players/");
						File[] playerDB=folder.listFiles();
						ArrayList<String> players=new ArrayList<String>();
						int playerCounter = 1;
						//println("test1");
						for(File s:playerDB)
						{
							if(s.isFile())
							{
								playerCounter++;
								players.add(s.getName());
							}
						}
						//println("test2");
						if(Interfaces.get(7).isValid() && Interfaces.get(7).getChild(14).getChildren().length >0)
						{
							//println("test3");
							//println(Interfaces.get(589).getChild(5).getChildren().length);
							for(int i=0;i<Interfaces.get(7).getChild(14).getChildren().length;i+=5)
							{
								//println("test4");
								String currentPlayer=Interfaces.get(7,14).getChild(i).getText().replace("<img=2>", "").replace("<img=0>", "").replace("<img=3>", "").replace("<img=1>", "").replaceAll(" ", " ").replaceAll(" ", " ");
								//println(currentPlayer);
								if(!players.contains(currentPlayer))
								{
									//println("True");
									String fullFileName=currentPlayer+".txt";
									File playerFile=new File(MainPath+"/players/"+fullFileName);
									if(!playerFile.exists())
									{
										try {
											playerFile.createNewFile();
											taskComplete=false;
											
											String nth=null;
											if(String.valueOf((playerCounter/2)+1).endsWith("1") && !String.valueOf((playerCounter/2)+1).equals("11"))
											{
												nth="st";
											}
											else if(String.valueOf((playerCounter/2)+1).endsWith("2") && !String.valueOf((playerCounter/2)+1).equals("12"))
											{
												nth="nd";
											}
											else if(String.valueOf((playerCounter/2)+1).endsWith("3") && !String.valueOf((playerCounter/2)+1).equals("13"))
											{
												nth="rd";
											}
											else
											{
												nth="th";
											}
											printMessage("Welcome, "+currentPlayer+"! You are the " + playerCounter + nth + " player to join.",true);
											taskComplete=true;
										} catch (IOException e) {
											println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

										}
									}
								}
							}
						}
						players.clear();
					}
					catch(Exception e)
					{
						println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

					}
					//println("Looping");
					General.sleep(3000);
				}
			}

		});
		Thread lastSeen=new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{

					try{
						/*ArrayList<String> storedNameNew=new ArrayList<String>();
						ArrayList<String> storedNameOld=new ArrayList<String>();
						ArrayList<Long> lastSeenNew=new ArrayList<Long>();
						ArrayList<Long> lastSeenOld=new ArrayList<Long>();*/
						if(Interfaces.get(7).isValid())
						{
							int totalComponents=Interfaces.get(7, 14).getChildren().length;
							//int totalNames=totalComponents/3;
							RSInterface[] players=Interfaces.get(7, 14).getChildren();
							for(int i=0;i<totalComponents;i+=5)
							{
								String playerName=players[i].getText().replace("<img=2>", "").replace("<img=3>", "").replace("<img=0>", "").replace("<img=1>", "").replaceAll(" ", " ").replaceAll(" ", " ");
								File playerFile=new File(MainPath+"/playersData/",playerName+".txt");
								if(!playerFile.exists())
								{
									playerFile.createNewFile();
									FileWriter newDate=new FileWriter(playerFile,false);
									newDate.write("lastseen:"+System.currentTimeMillis()+System.lineSeparator()+"time spent:0");
									newDate.close();
								}
								else{
									//date.format(System.currentTimeMillis())+" at "+time.format(System.currentTimeMillis())+" "+time.getTimeZone().toZoneId()+System.lineSeparator()+"time spent:0"
									Scanner scan=new Scanner(playerFile);
									int run=0;
									long lastDate=0L;
									long lastValue=0L;
									long newValue=0L;
									while(scan.hasNextLine())
									{
										Matcher matcher=Pattern.compile(":.*").matcher(scan.nextLine());
										if(matcher.find())
										{
											if(run==0)
											{
												lastDate=Long.valueOf(matcher.group(0).replaceFirst(":", ""));
											}
											if(run==1)
											{
												lastValue=Long.valueOf(matcher.group(0).replaceFirst(":", ""));
											}
										}
										run++;
									}
									scan.close();
									newValue=(System.currentTimeMillis()-lastDate)+lastValue;
									FileWriter updateDate=new FileWriter(playerFile,false);
									if(System.currentTimeMillis()-lastDate>=6000)
									{

										updateDate.write("lastseen:"+System.currentTimeMillis()+System.lineSeparator()+"time spent:"+lastValue);
										updateDate.close();
										//continue;
									}
									else{
										updateDate.write("lastseen:"+System.currentTimeMillis()+System.lineSeparator()+"time spent:"+newValue);
										updateDate.close();
									}
								}
							}
						}
					}
					catch(Exception e)
					{
						println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

					}
					General.sleep(1000);

				}
			}

		});
		Thread lastLogout=new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{

					try{
						while(pauseLoginData)
						{
							sleep(50);
						}
						ArrayList<String> listOfNames=new ArrayList<String>();
						listOfNames.clear();
						//int totalNames=totalComponents/3;
						File folder=new File(MainPath+"/playersData/");
						File[] fileList=folder.listFiles();
						for(File temp:fileList)
						{
							listOfNames.add(temp.getName().substring(0,temp.getName().length()-4));
						}
						RSInterface ccInterface=Interfaces.get(7, 14);

						int ccListLength=ccInterface.getChildren().length;
						if(ccListLength>=1)
						{
							for(int i=0;i<listOfNames.size();i++)
							{
								File lastLoginFile=new File(MainPath+"/playerLogin/",listOfNames.get(i)+".txt");
								if(!lastLoginFile.exists())
								{
									lastLoginFile.createNewFile();
								}
								FileWriter updateDate=new FileWriter(lastLoginFile,true);
								boolean online=false;
								for(int j=0;j<ccListLength;j+=5)
								{	
									if(listOfNames.get(i).equals(ccInterface.getChild(j).getText().replace("<img=2>", "").replace("<img=0>", "").replace("<img=1>", "").replace("<img=3>", "").replaceAll(" ", " ").replaceAll(" ", " ")))
									{
										online=true;
										String line="";
										Scanner scan=new Scanner(lastLoginFile);
										while(scan.hasNextLine())
										{
											line=scan.nextLine();
										}
										scan.close();
										if(!line.contains("Login"))
										{
											updateDate.write("Login:"+System.currentTimeMillis()+System.lineSeparator());
											updateDate.close();
										}
										break;
									}
								}
								if(!online)
								{
									String line="";
									Scanner scan=new Scanner(lastLoginFile);
									while(scan.hasNextLine())
									{
										line=scan.nextLine();
									}
									scan.close();
									if(!line.contains("Logout"))
									{
										updateDate.write("Logout:"+System.currentTimeMillis()+System.lineSeparator());
										updateDate.close();
									}
								}
							}				
						}
					}

					catch(Exception e)
					{
						println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }
					}
					General.sleep(1000);

				}
			}


		});
		Thread discord=new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{
					try
					{
						boolean command = false;
						File qFile = new File(MainPath + "/queue.txt");
						if(!qFile.exists())
						{
							qFile.createNewFile();
						}
						Scanner s = new Scanner(qFile);
						String lineToPrint = "";
						if(s.hasNextLine())
						{
							lineToPrint = s.nextLine();
						}
						if(!lineToPrint.isEmpty())
						{
							Matcher splitMsg = Pattern.compile("(.*):(.*)").matcher(lineToPrint);
							if(splitMsg.find())
							{
								if(splitMsg.groupCount() >= 2)
								{
									if(splitMsg.group(2).startsWith(" !!"))
									{
										command = true;
									}
								}
							}
							if(!command)
							{
								taskComplete=false;
								printMessage(lineToPrint,true);
								taskComplete=true;
							}
							String oldData = "";
							while(s.hasNextLine())
							{
								oldData += s.nextLine() + System.lineSeparator();
							}
							s.close();
							FileWriter fw = new FileWriter(qFile,false);
							fw.write(oldData);
							fw.close();
							if(command)
							{
								determineCommand("default_discord_user", splitMsg.group(2).replaceFirst(" ", "") , true);
							}
						}
					}
					catch(Exception e)
					{
						println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }
					}
					General.sleep(1000);

				}
			}


		});
		/*Thread lastLogout=new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{

					try{

							//int totalNames=totalComponents/3;
							File folder=new File(MainPath+"/playersData/");
							File[] fileList=folder.listFiles();
							for(File temp:fileList)
							{


									//date.format(System.currentTimeMillis())+" at "+time.format(System.currentTimeMillis())+" "+time.getTimeZone().toZoneId()+System.lineSeparator()+"time spent:0"
									Scanner scan=new Scanner(temp);
									long lastDate=0L;
									if(scan.hasNextLine())
									{

										Matcher matcher=Pattern.compile(":.*").matcher(scan.nextLine());
										if(matcher.find())
										{
												lastDate=Long.valueOf(matcher.group(0).replaceFirst(":", ""));
										}

									}
									scan.close();
									File lastLoginFile=new File(MainPath+"/playerLogin/",temp.getName());
									if(!lastLoginFile.exists())
									{
										lastLoginFile.createNewFile();
									}
									FileWriter updateDate=new FileWriter(lastLoginFile,true);
									if(System.currentTimeMillis()-lastDate>=3000)
									{
										String line="";
										scan=new Scanner(lastLoginFile);
										while(scan.hasNextLine())
										{
											line=scan.nextLine();
										}
										if(!line.contains("Logout"))
										{
										updateDate.write("Logout:"+System.currentTimeMillis()+System.lineSeparator());
										updateDate.close();
										}

									}
									else{
										String line="";
										scan=new Scanner(lastLoginFile);
										while(scan.hasNextLine())
										{
											line=scan.nextLine();
										}
										if(!line.contains("Login"))
										{
										updateDate.write("Login:"+System.currentTimeMillis()+System.lineSeparator());
										updateDate.close();
										}
									}
								}

						}

					catch(Exception e)
					{
						println("Exception at lastlogout thread");
						Logging("Exception at lastlogout thread",true);
					}
					General.sleep(1000);

				}
			}


		});*/
		Thread reminder2=new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{
					General.sleep(General.random(2600000, 4000000));
					try{
						while(!taskComplete)
						{

							sleep(100);
						}
						taskComplete=false;
						printMessage("Give our cc thread a bump! And vote in the !!comppoll",true);
						taskComplete=true;
					}
					catch(Exception e)
					{
						println("Exception at reminder for bump");
						Logging("Exception at reminder for bump",true);
					}

				}
			}

		});
		//date.setTimeZone(TimeZone.getTimeZone("UTC"));
		//time.setTimeZone(TimeZone.getTimeZone("UTC"));
		GameTab.open(TABS.CLAN);
		kickListener.start();
		extractNames.start();
		//Reminder.start();
		nameListener.start();
		lastSeen.start();
		lastLogout.start();
		reminder2.start();
		discord.start();
		Long clicker=System.currentTimeMillis();
		while(true)
		{
			if(taskComplete)
			{
				if(GameTab.getOpen()!=TABS.CLAN)
				{
					GameTab.open(TABS.CLAN);
				}
			}
			try{
				//println(nameListener.getState());
				if(Interfaces.get(7, 17).getText().equalsIgnoreCase("Join Chat"))
				{
					GameTab.open(TABS.CLAN);
					Clicking.click(Interfaces.get(7, 17));
					Logging("We left the CC, rejoin it!",true);
					Timing.waitCondition(new Condition(){

						@Override
						public boolean active() {
							return Interfaces.get(162, 32).getText().toLowerCase().contains("Enter the player name whose channel you wish to join".toLowerCase());
						}

					}, General.random(5000, 10000));
					if(Interfaces.get(162, 32).getText().toLowerCase().contains("Enter the player name whose channel you wish to join".toLowerCase())){
						Logging("Typing clan chat name", true);
						Keyboard.typeSend("os society");
						General.sleep(3000);
					}
				}
			}
			catch(Exception e)
			{
				println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

			}
			//println(extractNames.getState());
			if(extractNames.getState()!= State.RUNNABLE && extractNames.getState()!= State.TIMED_WAITING)
			{
				Logging("Name extractor terminated ",true);
				println("Thread for listening to names was terminated");
			}
			if((System.currentTimeMillis()-clicker)>=General.random(50000, 1200000))
			{
				DynamicClicking.clickRSTile(Player.getPosition(), "Walk here");
				clicker=System.currentTimeMillis();
			}
			/*println("populator state: "+CCListPopulator.getState());
			println("kick listener state: "+kickListener.getState());
			println("extract names state: "+extractNames.getState());
			println("Reminder state: "+Reminder.getState());*/
			if(GameTab.getOpen() != TABS.FRIENDS && GameTab.getOpen() != TABS.CLAN)
			{
				GameTab.open(TABS.CLAN);
			}
			if(Login.getLoginState() != Login.STATE.INGAME)
			{
				Login.login("osshelper51@mail.com", "shitonabrick");
				while(Login.getLoginState() != Login.STATE.INGAME)
				{
					General.sleep(100);
				}
				General.println("relogged, resetting time run");
				runtime=System.currentTimeMillis();
				General.sleep(1000);
				GameTab.open(TABS.CLAN);
			}
			if((System.currentTimeMillis()-runtime)>=21300000)
			{
				Logging("6 hour logout timer, relogging	",true);
				General.println("6 hour login timer, time to logout");
				Login.logout();
				while(Login.getLoginState() != Login.STATE.LOGINSCREEN)
				{
					General.sleep(100);
				}
				Login.login("osshelper51@mail.com", "shitonabrick");
				while(Login.getLoginState() != Login.STATE.INGAME)
				{
					General.sleep(100);
				}
				General.println("relogged, resetting time run");
				runtime=System.currentTimeMillis();
				General.sleep(1000);
				GameTab.open(TABS.CLAN);
			}
			General.sleep(500,700);
		}
	}
	private String getPlayerRank(RSInterfaceComponent player) {
		RSInterfaceChild CCList=Interfaces.get(7, 14);
		if(CCList != null)
		{
			int textureID=CCList.getChild(player.getIndex()+2).getTextureID();
			switch (textureID){
			case 1004:
				return "smiley";
			case 1012:
				return "1 banana";
			case 1011:
				return "2 banana";
			case 1010:
				return "3 banana";
			case 1009:
				return "bronze";
			case 1008:
				return "silver";
			case 1007:
				return "gold";
			case 1006:
				return "key";
			default:
				return "unranked";
			}
		}
		return null;
	}
	@Override
	public void clanMessageReceived(String name, String msg) {
		name=name.replace("<img=2>", "").replace("<img=0>", "").replace("<img=1>", "").replace("<img=3>", "").replace("<img=10>", "").replaceAll(" ", " ").replaceAll(" ", " ");
		if(recordChat)
		{
			try{
				String message = msg;
				//General.println(message);
				if(message.length() >=1)
				{
					URL url = new URL("https://discordapp.com/api/webhooks/256036445020618752/7AADcPs4VlNtjtCFD6Pw6I2UDujka5r7mnlvpjBI-YVxp1oZ4Q6LbhinO-bOSPcHR8ec");
					URLConnection con = url.openConnection();
					HttpURLConnection http = (HttpURLConnection)con;
					http.setRequestMethod("POST"); // PUT is another valid option
					http.setDoOutput(true);
					byte[] out = ("{\"username\":\"[OSRS]" + name + "\",\"content\":\"" + message.replaceAll("—", "") + "\",\"avatar_url\":\"http://i.imgur.com/AGhIk86.jpg\"}").getBytes(StandardCharsets.UTF_8);
					int length = out.length;
					http.setFixedLengthStreamingMode(length);
					http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
					http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
					http.connect();
					try(OutputStream os = http.getOutputStream()) {
						os.write(out);
					}
				}
				else
				{
					printMessage("Invalid message length.",true);
				}

			}catch(Exception e)
			{
				println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }
			}
		}
		if(pollRunning)
		{
			//println(voters);
			//println(votes);
			//println(msg);
			//println(name);
			//println("Message length = "+msg.length());
			if(!voters.contains(name) && msg.length()==1 && (msg.toLowerCase().startsWith("y") || msg.toLowerCase().startsWith("n")))
			{
				voters.add(name);
				votes.add(msg.toLowerCase());
			}
		}
		Thread commandManager=new Thread(new Runnable(){

			@Override
			public void run() {
				while(commandIssuer.size() >0)
				{
					Logging("Waiting for task to complete",true);
					int timer=0;
					while(!taskComplete && timer<=150)
					{
						println("Waiting for command to complete");
						sleep(100);
						timer++;
					}
					if(timer>=149)
					{
						Logging("Command timed out, resetting",true);
						commandIssuer.remove(0);
						command.remove(0);
					}
					Logging("Task completed",true);
					//println(command);
					//println(commandIssuer);
					//println("About to execute command");
					if(commandIssuer.size()>0)
					{
						Logging("issuer: "+commandIssuer+" Command: "+command,true);
						determineCommand(commandIssuer.get(0),command.get(0),true);
						commandIssuer.remove(0);
						command.remove(0);
						Logging("Command executed and removed",true);
					}
					GameTab.open(TABS.CLAN);
				}
			}
		});
		if(msg.toLowerCase().contains("Attempting to kick player from friends chat...".toLowerCase()))
		{
			//println("Returned true for kick message");
			Logging("Returned true for kick message",true);
			kickMessage=msg;
		}
		SimpleDateFormat date=new SimpleDateFormat("MMM d, YYYY");
		date.setTimeZone(TimeZone.getTimeZone("UTC"));
		file=new File(MainPath+"/OSS_LOGS/",date.format(System.currentTimeMillis())+".txt");
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
				println("New date started: "+file.getName());
			} catch (IOException e) {
				println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

			}
		}

		File playerFile=new File(MainPath+"/players/",name+".txt");
		if(playerFile.exists())
		{
			try {
				String[] messageSplit=msg.split(" ");
				int words=0;
				for(@SuppressWarnings("unused") String s:messageSplit)
				{
					words++;
				}
				ArrayList<String> backup=new ArrayList<String>();
				Scanner s=new Scanner(playerFile);
				while(s.hasNextLine())
				{
					backup.add(s.nextLine());
				}
				s.close();
				//println(backup);
				String charactersInfo="";
				String wordsInfo="";
				if(!backup.isEmpty())
				{
					charactersInfo=Files.readAllLines(Paths.get(MainPath+"/players/"+name+".txt")).get(0);
					wordsInfo=Files.readAllLines(Paths.get(MainPath+"/players/"+name+".txt")).get(1);
				}
				//println(charactersInfo);
				//println(wordsInfo);
				if(charactersInfo.isEmpty() && wordsInfo.isEmpty())
				{
					//println("New playerinfo");
					FileWriter playerUpdater=new FileWriter(playerFile,false);
					playerUpdater.write("total characters typed:"+msg.length()+System.lineSeparator()+"total words typed:"+words);
					playerUpdater.close();
				}
				if(!charactersInfo.isEmpty() && !wordsInfo.isEmpty())
				{
					//println("Existent playerinfo");
					String[] chars=charactersInfo.split(":");
					String[] totalWords=wordsInfo.split(":");
					//println(chars[1]+" "+totalWords[1]);
					int characters=0;
					try{
						characters=new Integer(chars[1]);
						words+=new Integer(totalWords[1]);

					}
					catch(NumberFormatException e)
					{
						println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

					}
					if(characters!=0)
					{
						//println("Rewriting to file");
						FileWriter playerUpdater=new FileWriter(playerFile,false);
						characters+=msg.length();
						//println(characters+" "+words);
						playerUpdater.write("total characters typed:"+characters+System.lineSeparator()+"total words typed:"+words);
						playerUpdater.close();
					}
				}
			} catch (IOException e) {
				println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

			}
		}
		try {
			FileWriter fw=new FileWriter(file,true);
			SimpleDateFormat time=new SimpleDateFormat("HH:mm:ss");
			time.setTimeZone(TimeZone.getTimeZone("UTC"));
			fw.write("["+time.format(System.currentTimeMillis())+"] "+name+": "+msg+System.lineSeparator());
			fw.close();
			//fw.flush();
		} catch (IOException e) {
			println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }

		}
		//println(name.toLowerCase().equals("OSS Bot".toLowerCase()));
		//println(name.toLowerCase());
		if(msg.startsWith("!!") && !name.equals(Player.getRSPlayer().getName()))
		{
			commandIssuer.add(name);
			command.add(msg);
			Logging(commandIssuer+" : "+command,true);
			if(!commandManager.isAlive())
			{
				commandManager.start();
			}

		}
		if(msg.contains("|\\/\\/\\/|"))
		{
			taskComplete=false;
			Keyboard.typeSend("/No.");
			taskComplete=true;
		}
		if(msg.contains("q p"))
		{
			taskComplete=false;
			Keyboard.setSpeed(0.01);
			Keyboard.typeSend("/No, "+name);
			taskComplete=true;
		}
	}
	private void determineCommand(String name, String command, boolean type) {
		//println(name + " "+command);
		taskComplete=false;
		found=false;
		parts.clear();
		parts2.clear();
		if(name.equals("default_discord_user") || getPlayer(name)!= null)
		{
			Logging("Player component retrieved",type);
			String player_rank;
			if(name.equals("default_discord_user"))
			{
				player_rank = "1 banana";
			}
			else
			{
				player_rank=getPlayerRank(getPlayer(name));
			}
			if((!player_rank.toLowerCase().equals("unranked".toLowerCase()) && !player_rank.equals(null)) || name.equals("xmax"))
			{
				String[] formatted=command.split("[^\\w+\\?]+");

				for(String s:formatted)
				{
					if(s!=null && s.length()>0)
					{
						parts.add(s);
					}
				}
				String[] formatted2=null;
				String item=null;
				String clue=null;
				String editText=null;
				String originalText = null;
				if(command.toLowerCase().startsWith("!!caps"))
				{
					originalText = command.substring(7);
				}
				if(command.toLowerCase().startsWith("!!calc"))
				{
					formatted2=command.replaceAll(",", "").toLowerCase().split("^!!calc");
				}
				if(command.toLowerCase().startsWith("!!edit examine"))
				{
					editText=command.substring(15);
				}
				if(command.toLowerCase().startsWith("!!cs") && parts.size()>=2 && parts.get(1).toLowerCase().contains("cryp"))
				{
					int memory=0;
					int p=0;
					for(int i=0;i<command.length();i++)
					{
						if(p>=2)
						{
							break;
						}
						char c = command.charAt(i);
						char z=' ';
						if(Character.valueOf(c).equals(Character.valueOf(z)))
						{
							p++;
							memory=i;
						}
					}
					clue=command.substring(memory+1);
					//println(clue);
				}
				if(command.toLowerCase().startsWith("!!examine"))
				{
					item=command.substring(10);
				}
				if(command.toLowerCase().startsWith("!!price"))
				{
					item=command.substring(8);
				}
				if(parts.size()>=1)
				{
					//time command

					if(parts.get(0).toLowerCase().equals("time"))
					{
						boolean isTZ=false;
						Logging("Time command detected",type);
						String[] tzList=TimeZone.getAvailableIDs();
						String TZ=null;
						if(parts.size()>=2 && command.length()>=8)
						{
							for(String tempTZ:tzList)
							{
								if(command.substring(7, command.length()).equalsIgnoreCase(tempTZ))
								{
									TZ=tempTZ;
									isTZ=true;
									break;
								}
							}
						}
						//General.println(parts);
						if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("comp"))
						{
							Logging("Trying to return competition time",type);
							//println("comp variable");
							Calendar now=Calendar.getInstance();
							now.setTimeZone(TimeZone.getTimeZone("UTC"));
							int weekday=now.get(Calendar.DAY_OF_WEEK);
							//println(weekday);
							if(now.get(Calendar.DAY_OF_WEEK)==7)
							{
								now.add(Calendar.DAY_OF_YEAR, 7);
							}
							int days=Calendar.SATURDAY-weekday;
							//println(days);
							//println(Calendar.SATURDAY);
							now.add(Calendar.DAY_OF_YEAR, days);

							now.set(Calendar.HOUR_OF_DAY,21);
							now.set(Calendar.MINUTE, 0);
							now.set(Calendar.SECOND,0);
							now.set(Calendar.MILLISECOND,0);
							//println(now.getTime());
							//println(Calendar.getInstance().getTime());
							//println(TimeUnit.MILLISECONDS.toMillis(Math.abs(now.getTimeInMillis()-Calendar.getInstance().getTimeInMillis())));
							long total=TimeUnit.MILLISECONDS.toMillis(Math.abs(now.getTimeInMillis()-Calendar.getInstance().getTimeInMillis()));
							if(total>=604800000)
							{
								now.add(Calendar.DAY_OF_YEAR, -7);
								total=TimeUnit.MILLISECONDS.toMillis(Math.abs(now.getTimeInMillis()-Calendar.getInstance().getTimeInMillis()));
							}
							int days2=(int) (total/86400000);
							long remainder=(total%86400000);
							int hours=(int) (remainder/3600000);
							remainder=remainder%3600000;
							int minutes=(int) (remainder/60000);
							remainder=remainder%60000;
							int seconds=(int) (remainder/1000);
							//println("about to print");
							printMessage(days2+" Days "+hours+" hours "+minutes+" minutes "+seconds+" seconds left till the competition ends",type);
							//println("printed");
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2 && isTZ)
						{
							SimpleDateFormat customTZ=new SimpleDateFormat("HH:mm:ss z");
							customTZ.setTimeZone(TimeZone.getTimeZone(TZ));
							//println(TZ);
							printMessage(customTZ.format(System.currentTimeMillis()),type);
							taskComplete=true;
							return;
						}
						else if(parts.size() ==1)
						{
							Logging("Trying to return time",type);
							SimpleDateFormat time=new SimpleDateFormat("HH:mm:ss z");
							time.setTimeZone(TimeZone.getTimeZone("UTC"));
							printMessage(time.format(System.currentTimeMillis()),type);
							taskComplete=true;
							return;

						}
						else{
							Logging("Invalid time command",type);
							printMessage("Valid paramters: [comp], leave it blank or a valid timezone",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("data"))
					{
						String pname="";
						if(parts.size()>=3)
						{
							for(int i=2;i<parts.size();i++)
							{
								if(i+1==parts.size())
								{
									pname+=parts.get(i);
									break;
								}
								pname+=parts.get(i)+" ";
							}
						}
						//println(pname);
						//println(pname.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_"));
						Logging("Data command detected",type);
						if(parts.size()>=2 && parts.get(1)!=null && parts.get(1).toLowerCase().equals("days") )
						{
							Logging("Data days detected",type);
							if(parts.size()>=3)
							{
								Logging("other person data [days]",type);
								int k=0;
								for(String s:list)
								{
									if(s.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(pname.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										found=true;
										/*if(pname.toLowerCase().equals("xmax"))
										{
											printMessage("m'lord has been a member for "+list.get(k+3)+" days",type);
											taskComplete=true;
											return;
										}*/
										if(list.get(k+3).toLowerCase().contains("unknown"))
										{
											printMessage(list.get(k) + " predates me.", type);
										}
										else
										{
										printMessage(list.get(k)+" has been a member for "+list.get(k+3)+" days",type);
										}
									}
									k++;
								}
								if(!found)
								{
									Logging("Other person not found in database",type);
									/*if(pname.toLowerCase().equals("xmax"))
									{
										printMessage("m'lord was not found in the records.",type);
										taskComplete=true;
										return;
									}*/
									printMessage(pname+" was not found in our records",type);
								}
								taskComplete=true;
								return;
							}
							else if(parts.size()==2){
								Logging("own data [days]",type);
								int k=0;
								for(String s:list)
								{
									if(s.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(name.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										found=true;
										/*if(name.toLowerCase().equals("xmax"))
										{
											printMessage("m'lord, you have been a member for "+list.get(k+3)+" days",type);
											taskComplete=true;
											return;
										}*/
										if(list.get(k+3).toLowerCase().contains("unknown"))
										{
											printMessage("You predate me.", type);
										}
										else
										{
											printMessage("You have been a member for "+list.get(k+3)+" days",type);
										}
									}
									k++;
								}
								if(!found)
								{
									Logging("own data not found [days]",type);
									/*if(name.toLowerCase().equals("xmax"))
									{
										printMessage("m'lord, you were not found in the records.",type);
										taskComplete=true;
										return;
									}*/
									printMessage("You were not found in our records",type);
								}
								taskComplete=true;
								return;
							}
						}
						else if(parts.size()>=2 && parts.get(1)!=null && parts.get(1).toLowerCase().equals("time"))
						{
							Logging("time spent command",type);
							if(parts.size()>=3)
							{
								String temppName="";
								for(int i=2;i<parts.size();i++)
								{
									if(parts.size()-1==i)
									{
										temppName+=parts.get(i);
									}
									else{
										temppName+=parts.get(i)+" ";
									}
								}
								temppName=temppName.toLowerCase().replaceAll(" ", "_");
								try{

									File folder=new File(MainPath+"/playersData/");
									File[] playerDB=folder.listFiles();
									//String lastseen="";
									String realName="";
									String timeSpent="";
									boolean playerFound=false;
									for(File temp:playerDB)
									{
										if(temp.getName().substring(0,temp.getName().length()-4).replaceAll(" ", "_").equalsIgnoreCase(temppName))
										{
											realName=temp.getName().substring(0,temp.getName().length()-4);
											playerFound=true;
											Scanner scan=new Scanner(temp);
											while(scan.hasNextLine())
											{
												timeSpent=scan.nextLine();
											}
											scan.close();
											break;
										}
									}
									if(playerFound)
									{
										Matcher matcher=Pattern.compile(":.*").matcher(timeSpent);
										//Matcher matcher2=Pattern.compile(":.*").matcher(timeSpent);
										Logging("Found player data on lastseen command",type);
										if(matcher.find())
										{
											long total=Long.valueOf(matcher.group(0).replaceFirst(":", ""));
											//println(matcher.groupCount());
											int days=(int) (total/86400000);
											long remainder=(total%86400000);
											int hours=(int) (remainder/3600000);
											remainder=remainder%3600000;
											int minutes=(int) (remainder/60000);
											remainder=remainder%60000;
											int seconds=(int) (remainder/1000);
											printMessage(realName+" has spent "+days+" Days "+hours+" Hours "+minutes+" Minutes "+seconds+" Seconds in this cc.",type);
											taskComplete=true;
											return;
										}

									}
									else
									{
										Logging("Couldn't find player data time spent command",type);
										printMessage("couldn't find player data.",type);
										taskComplete=true;
										return;
									}
								}
								catch(Exception e)
								{
									println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

								}
							}
							else if(parts.size()==2)
							{
								String temppName=name.replaceAll(" ", "_").replaceAll(" ", "_");

								temppName=temppName.toLowerCase().replaceAll(" ", "_");
								try{

									File folder=new File(MainPath+"/playersData/");
									File[] playerDB=folder.listFiles();
									//String lastseen="";
									//String realName="";
									String timeSpent="";
									boolean playerFound=false;
									for(File temp:playerDB)
									{
										if(temp.getName().substring(0,temp.getName().length()-4).replaceAll(" ", "_").equalsIgnoreCase(temppName))
										{
											//realName=temp.getName().substring(0,temp.getName().length()-4);
											playerFound=true;
											Scanner scan=new Scanner(temp);
											while(scan.hasNextLine())
											{
												timeSpent=scan.nextLine();
											}
											scan.close();
											break;
										}
									}
									if(playerFound)
									{
										Matcher matcher=Pattern.compile(":.*").matcher(timeSpent);
										//Matcher matcher2=Pattern.compile(":.*").matcher(timeSpent);
										Logging("Found player data on lastseen command",type);
										if(matcher.find())
										{
											long total=Long.valueOf(matcher.group(0).replaceFirst(":", ""));
											//println(matcher.groupCount());
											int days=(int) (total/86400000);
											long remainder=(total%86400000);
											int hours=(int) (remainder/3600000);
											remainder=remainder%3600000;
											int minutes=(int) (remainder/60000);
											remainder=remainder%60000;
											int seconds=(int) (remainder/1000);
											printMessage("You have spent "+days+" Days "+hours+" Hours "+minutes+" Minutes "+seconds+" Seconds in this cc.",type);
											taskComplete=true;
											return;
										}

									}
									else
									{
										Logging("Couldn't find player data time spent command",type);
										printMessage("couldn't find player data.",type);
										taskComplete=true;
										return;
									}
								}
								catch(Exception e)
								{
									println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

								}
							}
							else{
								Logging("Something went wrong in data time",type);
								printMessage("Something went wrong in this command.",type);
								taskComplete=true;
								return;
							}
						}
						else if(parts.size()>=2 && parts.get(1)!=null && parts.get(1).toLowerCase().equals("chars") )
						{
							Logging("Data characters detected",type);
							if(parts.size()>=3)
							{

								Logging("other person data [characters]",type);
								File folder=new File(MainPath+"/players/");
								File[] playerDB=folder.listFiles();
								for(File s:playerDB)
								{
									if(s.getName().substring(0,s.getName().length()-4).toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(pname.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{

										try {
											File playerinfo=new File(MainPath+"/players/",s.getName());
											Scanner scan;
											scan = new Scanner(playerinfo);
											String lines="";
											while(scan.hasNextLine())
											{
												lines+=scan.nextLine();
											}
											scan.close();
											int charsNumber=0;
											if(!lines.isEmpty())
											{
												String chars=Files.readAllLines(Paths.get(MainPath+"/players/"+s.getName())).get(0);
												try{
													String[] charsSplit=chars.split(":");
													charsNumber=new Integer(charsSplit[1]);

												}
												catch(NumberFormatException e)
												{
													println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

												}
												if(charsNumber!=0)
												{
													/*if(pname.toLowerCase().equals("xmax"))
													{
														printMessage("m'lord has typed "+charsNumber+" characters in this cc.",type);
														found=true;
														taskComplete=true;
														return;
													}*/
													printMessage(s.getName().substring(0,s.getName().length()-4)+" has typed "+charsNumber+" characters in this cc.",type);
													found=true;
													taskComplete=true;
													return;
												}
												else{
													/*if(pname.toLowerCase().equals("xmax"))
													{
														printMessage("m'lord's total characters data could not be found.",type);
														found=true;
														taskComplete=true;
														return;
													}*/
													printMessage(s.getName().substring(0,s.getName().length()-4)+"'s total character data could not be found",type);
													found=true;
													taskComplete=true;
													return;
												}
											}
										} catch (IOException e) {
											println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

										}

									}
								}
								if(!found)
								{
									Logging("Other person not found in database",type);
									/*if(pname.toLowerCase().equals("xmax"))
									{
										printMessage("m'lord was not found in the records.",type);
										taskComplete=true;
										return;
									}*/
									printMessage(pname+" was not found in our records",type);
								}
								taskComplete=true;
								return;
							}
							else if(parts.size()==2){
								Logging("own data [chars]",type);
								File folder=new File(MainPath+"/players/");
								File[] playerDB=folder.listFiles();
								//println(name.toLowerCase().replaceAll(" ", "_"));
								for(File s:playerDB)
								{
									//println(s.getName().toLowerCase().replaceAll(" ", "_"));
									if(s.getName().substring(0,s.getName().length()-4).toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(name.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										//println("Found player");
										try {
											File playerinfo=new File(MainPath+"/players/",s.getName());
											Scanner scan;
											scan = new Scanner(playerinfo);
											String lines="";
											while(scan.hasNextLine())
											{
												lines+=scan.nextLine();
											}
											scan.close();
											int charsNumber=0;
											if(!lines.isEmpty())
											{
												String chars=Files.readAllLines(Paths.get(MainPath+"/players/"+s.getName())).get(0);
												try{
													String[] charsSplit=chars.split(":");
													charsNumber=new Integer(charsSplit[1]);

												}
												catch(NumberFormatException e)
												{
													println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

												}
												if(charsNumber!=0)
												{
													/*if(name.toLowerCase().equals("xmax"))
													{
														printMessage("m'lord, you have typed "+charsNumber+" characters in this cc.",type);
														found=true;
														taskComplete=true;
														return;
													}*/
													printMessage("You have typed "+charsNumber+" characters in this cc.",type);
													found=true;
													taskComplete=true;
													return;
												}
												else{
													/*if(name.toLowerCase().equals("xmax"))
													{
														printMessage("m'lord, your total characters data could not be found.",type);
														found=true;
														taskComplete=true;
														return;
													}*/
													printMessage("Your total characters data could not be found",type);
													found=true;
													taskComplete=true;
													return;
												}
											}
										} catch (IOException e) {

											println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

										}

									}
								}
								if(!found)
								{
									Logging("own data not found [chars]",type);
									/*if(name.toLowerCase().equals("xmax"))
									{
										printMessage("m'lord, you were not found in the records.",type);
										taskComplete=true;
										return;
									}*/
									printMessage("You were not found in our records",type);
								}
								taskComplete=true;
								return;
							}
						}
						else if(parts.size()>=2 && parts.get(1)!=null && parts.get(1).toLowerCase().equals("words") )
						{
							Logging("Data characters detected",type);
							if(parts.size()>=3)
							{

								Logging("other person data [words]",type);
								File folder=new File(MainPath+"/players/");
								File[] playerDB=folder.listFiles();
								for(File s:playerDB)
								{
									if(s.getName().substring(0,s.getName().length()-4).toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(pname.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{

										try {
											File playerinfo=new File(MainPath+"/players/",s.getName());
											Scanner scan;
											scan = new Scanner(playerinfo);
											String lines="";
											while(scan.hasNextLine())
											{
												lines+=scan.nextLine();
											}
											scan.close();
											int charsNumber=0;
											if(!lines.isEmpty())
											{
												String chars=Files.readAllLines(Paths.get(MainPath+"/players/"+s.getName())).get(1);
												try{
													String[] charsSplit=chars.split(":");
													charsNumber=new Integer(charsSplit[1]);

												}
												catch(NumberFormatException e)
												{
													println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

												}
												if(charsNumber!=0)
												{
													/*if(pname.toLowerCase().equals("xmax"))
													{
														printMessage("m'lord has typed "+charsNumber+" words in this cc.",type);
														found=true;
														taskComplete=true;
														return;
													}*/
													printMessage(s.getName().substring(0,s.getName().length()-4)+" has typed "+charsNumber+" words in this cc.",type);
													found=true;
													taskComplete=true;
													return;
												}
												else{
													/*if(pname.toLowerCase().equals("xmax"))
													{
														printMessage("m'lord's total words data could not be found",type);
														found=true;
														taskComplete=true;
														return;
													}*/
													printMessage(s.getName().substring(0,s.getName().length()-4)+"'s total words data could not be found",type);
													found=true;
													taskComplete=true;
													return;
												}
											}
										} catch (IOException e) {

											println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

										}

									}
								}
								if(!found)
								{
									Logging("Other person not found in database",type);
									/*if(pname.toLowerCase().equals("xmax"))
									{

										printMessage("m'lord was not found in the records.",type);
										taskComplete=true;
										return;
									}*/
									printMessage(pname+" was not found in our records",type);
								}
								taskComplete=true;
								return;
							}
							else if(parts.size()==2){
								Logging("own data [words]",type);
								File folder=new File(MainPath+"/players/");
								File[] playerDB=folder.listFiles();
								//println(name.toLowerCase().replaceAll(" ", "_"));
								for(File s:playerDB)
								{
									//println(s.getName().toLowerCase().replaceAll(" ", "_"));
									if(s.getName().substring(0,s.getName().length()-4).toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(name.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										//println("Found player");
										try {
											File playerinfo=new File(MainPath+"/players/",s.getName());
											Scanner scan;
											scan = new Scanner(playerinfo);
											String lines="";
											while(scan.hasNextLine())
											{
												lines+=scan.nextLine();
											}
											scan.close();
											int charsNumber=0;
											if(!lines.isEmpty())
											{
												String chars=Files.readAllLines(Paths.get(MainPath+"/players/"+s.getName())).get(1);
												try{
													String[] charsSplit=chars.split(":");
													charsNumber=new Integer(charsSplit[1]);

												}
												catch(NumberFormatException e)
												{
													println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

												}
												if(charsNumber!=0)
												{
													/*if(name.toLowerCase().equals("xmax"))
													{
														printMessage("m'lord, you have typed "+charsNumber+" words in this cc.",type);
														found=true;
														taskComplete=true;
														return;
													}*/
													printMessage("You have typed "+charsNumber+" words in this cc.",type);
													found=true;
													taskComplete=true;
													return;
												}
												else{
													/*if(name.toLowerCase().equals("xmax"))
													{
														printMessage("m'lord, your total words data could not be found",type);
														found=true;
														taskComplete=true;
														return;
													}*/
													printMessage("Your total words data could not be found",type);
													found=true;
													taskComplete=true;
													return;
												}
											}
										} catch (IOException e) {

											println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

										}

									}
								}
								if(!found)
								{
									Logging("own data not found [words]",type);
									/*if(name.toLowerCase().equals("xmax"))
									{
										printMessage("m'lord, you were not found in the records.",type);
										taskComplete=true;
										return;
									}*/
									printMessage("You were not found in our records",type);
								}
								taskComplete=true;
								return;
							}
						}
						else if(parts.size()>=2 && parts.get(1)!=null && parts.get(1).toLowerCase().equals("joindate"))
						{
							Logging("Joindata parameters",type);
							if(parts.size()>=3)
							{
								Logging("Other person data [joindate]",type);
								int k=0;
								for(String s:list)
								{
									if(s.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(pname.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										found=true;
										if(list.get(k+2).isEmpty())
										{
											/*if(pname.toLowerCase().equals("xmax"))
											{
												printMessage("m'lord has an unknown join date.",type);
												taskComplete=true;
												return;
											}*/
											printMessage(list.get(k)+" predates me",type);
										}
										else{
											/*if(pname.toLowerCase().equals("xmax"))
											{
												printMessage("m'lord joined the cc on: "+list.get(k+2),type);
												taskComplete=true;
												return;
											}*/
											printMessage(list.get(k)+" joined the cc on: "+list.get(k+2),type);
										}
									}
									k++;
								}
								if(!found)
								{
									Logging("Other person not found in db[joindate]",type);
									/*if(pname.toLowerCase().equals("xmax"))
									{
										printMessage("m'lord was not found in the records.",type);
										taskComplete=true;
										return;
									}*/
									printMessage(pname+" was not found in our records",type);
								}
								taskComplete=true;
								return;
							}
							else if(parts.size()==2)
							{
								Logging("Own person data [joindate]",type);
								int k=0;
								for(String s:list)
								{
									if(s.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(name.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										found=true;
										if(list.get(k+2).isEmpty())
										{
											/*if(name.toLowerCase().equals("xmax"))
											{
												printMessage("m'lord, your joindate is unknown.",type);
												taskComplete=true;
												return;
											}*/
											printMessage("You predate me.",type);
										}
										else{
											/*if(name.toLowerCase().equals("xmax"))
											{
												printMessage("m'lord, you joined the cc on: "+list.get(k+2),type);
												taskComplete=true;
												return;
											}*/
											printMessage("You joined the cc on:  "+list.get(k+2),type);
										}
									}
									k++;
								}
								if(!found)
								{

									Logging("Own person not found [joindate]",type);
									/*if(name.toLowerCase().equals("xmax"))
									{
										printMessage("m'lord, you were not found in the records.",type);
										taskComplete=true;
										return;
									}*/
									printMessage("You were not found in our records",type);
								}
								taskComplete=true;
								return;
							}
							else{
								Logging("incorrect parameters in data [joindate]",type);
								printMessage("invalid parameters",type);
								taskComplete=true;
								return;
							}
						}
						else if(parts.size()>=2 && parts.get(1)!=null && parts.get(1).toLowerCase().equals("rank") )
						{
							Logging("Data rank detected",type);
							if(parts.size()>=3)
							{
								Logging("Other person data [rank]",type);
								int k=0;
								for(String s:list)
								{
									if(s.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(pname.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										//println(list.get(k+1));
										int number=new Integer(list.get(k+1)).intValue();
										found=true;
										if(number == 0)
										{
											printMessage(list.get(k)+"'s rank is none",type);
										}
										else if(number == 1)
										{
											printMessage(list.get(k)+"'s rank is 1 banana",type);
										}
										else if(number == 2)
										{
											printMessage(list.get(k)+"'s rank is 2 banana",type);
										}
										else if(number == 3)
										{
											printMessage(list.get(k)+"'s rank is 3 banana",type);
										}
										else if(number == 4)
										{
											printMessage(list.get(k)+"'s rank is bronze star",type);
										}
										else if(number == 5)
										{
											printMessage(list.get(k)+"'s rank is silver star",type);
										}
										else if(number == 6)
										{
											/*if(pname.toLowerCase().equals("xmax"))
											{
												printMessage("m'lord's rank is gold star",type);
												taskComplete=true;
												return;
											}*/
											printMessage(list.get(k)+"'s rank is gold star",type);
										}
										else if(number == 7)
										{
											printMessage(list.get(k)+"'s rank is smiley",type);
										}
										else{
											printMessage(list.get(k)+"'s rank is unknown",type);
										}
									}
									k++;
								}
								if(!found)
								{
									Logging("Other person not found [rank]",type);
									/*if(pname.toLowerCase().equals("xmax"))
									{
										printMessage("m'lord was not found in the records.",type);
										taskComplete=true;
										return;
									}*/
									printMessage(pname+" was not found in our records",type);
								}
								taskComplete=true;
								return;
							}
							else if(parts.size()==2)
							{
								Logging("Own data [rank]",type);
								int k=0;
								for(String s:list)
								{
									//println(name.toLowerCase().replaceAll(" ", "_"));
									if(s.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_").equals(name.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										int number=new Integer(list.get(k+1)).intValue();
										found=true;
										if(number == 0)
										{
											printMessage("Your rank is none",type);
										}
										else if(number ==1)
										{
											printMessage("Your rank is 1 banana",type);
										}
										else if(number ==2)
										{
											printMessage("Your rank is 2 banana",type);
										}
										else if(number ==3)
										{
											printMessage("Your rank is 3 banana",type);
										}
										else if(number ==4)
										{
											printMessage("Your rank is bronze star",type);
										}
										else if(number == 5)
										{
											printMessage("Your rank is silver star",type);
										}
										else if(number == 6)
										{
											/*if(name.toLowerCase().equals("xmax"))
											{
												printMessage("m'lord, your rank is gold star.",type);
												taskComplete=true;
												return;
											}*/
											printMessage("Your rank is gold star",type);
										}
										else if(number == 7)
										{
											printMessage("Your rank is a smiley",type);
										}
										else{
											printMessage("Your rank is unknown",type);
										}
									}
									k++;
								}
								if(!found)
								{
									Logging("Own data not found [rank]",type);
									printMessage("You were not found in our records",type);
								}
								taskComplete=true;
								return;
							}
						}
						else if(parts.size()>=2 && parts.get(1).equalsIgnoreCase("comprank"))
						{
							//println("test");
							try{
								ArrayList<String> ranks=new ArrayList<String>();
								Scanner scanner2 = new Scanner(new File("Top_comp.txt"));
								scanner2.useDelimiter("[\n]");
								int i =0;
								while (scanner2.hasNextLine()) {
									String line = scanner2.nextLine();
									if(i == 0 || i%4==0)
									{
										ranks.add(line);
										scanner2.nextLine();
										scanner2.nextLine();
										line = scanner2.nextLine();
										ranks.add(NumberFormat.getInstance().format(Integer.valueOf(line)));
										i+=3;
									}
									i++;
									// you're at the end of the line here. Do what you have to do.
								}
								scanner2.close();
								if(parts.size()>=3)
								{
									if(ranks.indexOf(pname.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_"))!=-1)
									{
										int rank=ranks.indexOf(pname.replaceAll(" ", "_").replaceAll(" ", "_").toLowerCase());
										//println(rank);
										String nth=null;
										if(String.valueOf((rank/2)+1).endsWith("1") && !String.valueOf((rank/2)+1).equals("11"))
										{
											nth="st";
										}
										else if(String.valueOf((rank/2)+1).endsWith("2") && !String.valueOf((rank/2)+1).equals("12"))
										{
											nth="nd";
										}
										else if(String.valueOf((rank/2)+1).endsWith("3") && !String.valueOf((rank/2)+1).equals("13"))
										{
											nth="rd";
										}
										else
										{
											nth="th";
										}
										printMessage(ranks.get(rank)+" is currently "+((rank/2)+1)+nth+" with "+ranks.get(rank+1)+" xp",type);
										taskComplete=true;
										return;
									}
									else
									{
										printMessage("Couldn't find competition rank for "+pname,type);
										taskComplete=true;
										return;
									}
								}
								else
								{
									if(ranks.indexOf(name.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_"))!=-1)
									{
										int rank=ranks.indexOf(name.toLowerCase().replaceAll(" ", "_").replaceAll(" ", "_"));
										String nth=null;
										if(String.valueOf((rank/2)+1).endsWith("1") && !String.valueOf((rank/2)+1).equals("11"))
										{
											nth="st";
										}
										else if(String.valueOf((rank/2)+1).endsWith("2") && !String.valueOf((rank/2)+1).equals("12"))
										{
											nth="nd";
										}
										else if(String.valueOf((rank/2)+1).endsWith("3") && !String.valueOf((rank/2)+1).equals("13"))
										{
											nth="rd";
										}
										else
										{
											nth="th";
										}
										printMessage("You are currently "+((rank/2)+1)+nth+" with "+ranks.get(rank+1)+" xp",type);
										taskComplete=true;
										return;
									}
									else
									{
										printMessage("Couldn't find competition rank for your name.",type);
										taskComplete=true;
										return;
									}
								}
							}
							catch(Exception e)
							{
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
						}
						else{
							Logging("invalid parameters for data command",type);
							printMessage("Valid parameters: days, joindate, rank, chars, words, time, comprank with playername after or leave it blank",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("test"))
					{
						if(name.equalsIgnoreCase("xmax") || name.equalsIgnoreCase("cynes"))
						{
							printMessage("I am working",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("cs"))
					{
						Logging("Clue solver command",type);
						if(parts.size()>=2 && parts.get(1).toLowerCase().contains("anag"))
						{
							String anagram="";
							for(int i=2;i<parts.size();i++)
							{
								if(i+1==parts.size())
								{
									anagram+=parts.get(i);
									break;
								}
								anagram+=parts.get(i)+" ";
							}
							try {
								Scanner scan=new Scanner(new File(MainPath+"/cluesolver/","Anagrams.txt"));
								//int ln=0;
								String[] temp=null;
								boolean called=false;
								while(scan.hasNextLine())
								{
									temp=scan.nextLine().toLowerCase().split(":");
									if(temp[0].equalsIgnoreCase(anagram.toLowerCase()))
									{
										called=true;
										break;
									}
								}
								scan.close();
								if(called)
								{
									printMessage(temp[2]+" [NPC: "+temp[1]+"]"+"[Answer: "+temp[3]+"]",type);
									taskComplete=true;
									return;
								}
								else{
									printMessage("Couldn't find anagram.",type);
									taskComplete=true;
									return;
								}
							} catch (IOException e) {

								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

							}
						}
						else if(parts.size()>=2 && parts.get(1).toLowerCase().contains("cryp"))
						{
							String cryptic=clue;
							try {
								Scanner scan=new Scanner(new File("cluesolver/","cryptic.txt"));
								String[] s=null;
								boolean called=false;
								while(scan.hasNextLine())
								{
									s=scan.nextLine().toLowerCase().split(":");
									//System.out.println(s);
									if(s[0].contains(cryptic.toLowerCase()))
									{
										called=true;
										break;
									}
								}
								scan.close();
								if(called)
								{
									printMessage(s[1],type);
									taskComplete=true;
									return;
								}
								else{
									printMessage("Couldn't find cryptic answer.",type);
									taskComplete=true;
									return;
								}
							} catch (IOException e) {
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

							}
						}
						else if(parts.size()>=2 && parts.get(1).toLowerCase().contains("cip"))
						{
							String cipher="";
							for(int i=2;i<parts.size();i++)
							{
								if(i+1==parts.size())
								{
									cipher+=parts.get(i);
									break;
								}
								cipher+=parts.get(i)+" ";
							}
							try {
								Scanner scan=new Scanner(new File("cluesolver/","Ciphers.txt"));
								String[] s=null;
								boolean called=false;
								while(scan.hasNextLine())
								{
									s=scan.nextLine().toLowerCase().split(":");
									//System.out.println(s);
									if(s[0].equalsIgnoreCase(cipher.toLowerCase()))
									{
										called=true;
										break;
									}
								}
								scan.close();
								if(called)
								{
									printMessage(s[2]+" [NPC: "+s[1]+"][Answer: "+s[3]+"]",type);
									taskComplete=true;
									return;
								}
								else{
									printMessage("Couldn't decipher.",type);
									taskComplete=true;
									return;
								}
							} catch (IOException e) {

								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

							}
						}
						else{
							printMessage("Invalid paramters. !!help cs",type);
							taskComplete=true;
							return;
						}
					}
					/*else if(parts.get(0).toLowerCase().equals("events"))
					{
						Logging("Events command detected",type);
						if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("add"))
						{
							Logging("Events [add] command",type);
							if(player_rank.equals("1 banana") || player_rank.equals("2 banana") || player_rank.equals("unranked") || player_rank.equals(null))
							{
								Logging("events command rank not high enough [add]",type);
								printMessage("You don't have permissions to do that",type);
								taskComplete=true;
								return;
							}
							else
							{
								Logging("events command persons rank is high enough [add]",type);
								if(parts.size()>=4)
								{
									if(eventCreator(name,parts))
									{
										Logging("Event created [add]",type);
										printMessage("Created event successfully. !!events for list of events",type);
										taskComplete=true;
										return;
									}
									else{
										Logging("Invalid event parameters [add]",type);
										printMessage("Invalid parameters. !!events add name time details",type);
										taskComplete=true;
										return;
									}
								}
								else{
									Logging("Invalid event parameters [add]2",type);
									printMessage("Invalid parameters. !!events add (name) (in x mins) (details)",type);
									taskComplete=true;
									return;
								}
							}
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("remove"))
						{
							Logging("Event command [remove] detected",type);
							if(player_rank.equals("1 banana") || player_rank.equals("2 banana") || player_rank.equals("unranked") || player_rank.equals(null))
							{
								Logging("event command rank not high enough [remove]",type);
								printMessage("You don't have permissions to do that",type);
								taskComplete=true;
								return;
							}
							if(parts.size()>=3 && parts.get(2) != "" && parts.get(2) != null)
							{
								if(eventMap.get(parts.get(2).toLowerCase()) != null)
								{
									eventMap.remove(parts.get(2).toLowerCase());
									Logging("Event removed [remove]",type);
									printMessage("Removed the event",type);
									taskComplete=true;
									return;
								}
								else{
									Logging("Event didn't exist [remove]",type);
									printMessage("Event doesn't exist by that name",type);
									taskComplete=true;
									return;
								}
							}
							else{
								Logging("Invalid event name [remove]",type);
								printMessage("Not a valid event name",type);
								taskComplete=true;
								return;
							}
						}
						else if(parts.size()==1){
							Logging("event listing detected",type);
							if(!eventMap.isEmpty())
							{
								Logging("Events were found",type);
								for(Entry<String, ArrayList<String>> temp:eventMap.entrySet())
								{
									ArrayList<String> detail=temp.getValue();
									String eventName=temp.getKey();

									printMessage(eventName+ " by "+detail.get(0)+" @ "+fullFormat.format(Long.parseLong(detail.get(3))+(Long.parseLong(detail.get(1))*60000))+" : "+detail.get(2),type);
								}
								taskComplete=true;
								return;
							}
							else{
								Logging("No events found",type);
								printMessage("No events found :s",type);
								taskComplete=true;
								return;
							}
						}
					}*/
					else if(parts.get(0).toLowerCase().equals("top"))
					{
						Logging("Top command detected",type);
						if(parts.size()>=2 && parts.get(1).toLowerCase().equals("comp"))
						{
							Scanner scanner2;
							List<String> compRecords = new ArrayList<String>();
							try {
								scanner2 = new Scanner(new File("Top_comp.txt"));
								scanner2.useDelimiter("[\n]");
								int i=0;
								while (scanner2.hasNextLine()) {
									String line = scanner2.nextLine();

									if(i == 0 || i%4==0)
									{
										compRecords.add(line);
										scanner2.nextLine();
										scanner2.nextLine();
										line = scanner2.nextLine();
										compRecords.add(NumberFormat.getInstance().format(Integer.valueOf(line)));
										i+=3;
									}
									i++;
									// you're at the end of the line here. Do what you have to do.
								}
								scanner2.close();
							}
							catch(Exception e)
							{

								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
							if(compRecords.size()>=6)
							{
								Logging("Listing top 3",type);
								printMessage("[1: "+compRecords.get(0)+" +"+compRecords.get(1)+"][2: "+compRecords.get(2)+" +"+compRecords.get(3)+"][3: "+compRecords.get(4)+" +"+compRecords.get(5)+"]",type);
								taskComplete=true;
								return;
							}
							else{
								Logging("Error getting top 3 size of variable: "+compRecords.size(),type);
								printMessage("Error occured trying to get the top 3, try again",type);
								taskComplete=true;
								return;
							}
						}
						else if(parts.size()>=2 && parts.get(1).toLowerCase().equals("chars"))
						{
							Logging("Top chars command",type);
							try{
								File folder=new File(MainPath+"/players/");
								File[] playerDB=folder.listFiles();
								int characters=0;
								//int charstemp=0;
								int first=0;
								int second=0;
								int third=0;
								String firstName="";
								String secondName="";
								String thirdName="";
								ArrayList<Integer> listed=new ArrayList<Integer>();
								ArrayList<String> listedName=new ArrayList<String>();
								listed.clear();
								listedName.clear();
								//int counter=0;
								//println("Top chars primary stuff done");
								for(File s:playerDB)
								{
									if(s.getName().substring(0,s.getName().length()-4).replaceAll(" ", "_").replaceAll(" ", "_").equalsIgnoreCase(Player.getRSPlayer().getName().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										continue;
									}
									Scanner scan=new Scanner(s);
									if(scan.hasNextLine())
									{
										String chars=Files.readAllLines(Paths.get(MainPath+"/players/"+s.getName())).get(0);
										//println(chars);
										if(!chars.isEmpty())
										{
											try
											{
											characters=new Integer(chars.split(":")[1]);
											//println(characters);
											listed.add(characters);
											//println(listed);
											listedName.add(s.getName().substring(0, s.getName().length()-4));
											//println(listedName);
											}
											catch(Exception e)
											{
												println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

											}
										}
									}
									scan.close();
								}
								//println(listed.size());
								for(int i=0;i<listed.size();i++)
								{
									if(listed.get(i)>third)
									{
										if(listed.get(i)>second)
										{
											if(listed.get(i)>first)
											{
												thirdName=secondName;
												secondName=firstName;
												firstName=listedName.get(i);
												third=second;
												second=first;
												first=listed.get(i);
												continue;
											}
											third=second;
											thirdName=secondName;
											second=listed.get(i);
											secondName=listedName.get(i);
											continue;
										}
										third=listed.get(i);
										thirdName=listedName.get(i);
									}
								}
								//Collections.sort(listed,Collections.reverseOrder());
								//println("after second loop");
								printMessage("[1: "+firstName+" = "+first+"][2: "+secondName+" = "+second+"][3: "+thirdName+" = "+third+"]",type);
								taskComplete=true;
								return;
							}
							catch(Exception e)
							{

								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
						}
						else if(parts.size()>=2 && parts.get(1).toLowerCase().equals("words"))
						{
							Logging("Top words command",type);
							try{
								File folder=new File(MainPath+"/players/");
								File[] playerDB=folder.listFiles();
								int characters=0;
								//int charstemp=0;
								int first=0;
								int second=0;
								int third=0;
								String firstName="";
								String secondName="";
								String thirdName="";
								ArrayList<Integer> listed=new ArrayList<Integer>();
								ArrayList<String> listedName=new ArrayList<String>();
								listed.clear();
								listedName.clear();
								//int counter=0;
								//println("Top chars primary stuff done");
								for(File s:playerDB)
								{
									if(s.getName().substring(0,s.getName().length()-4).replaceAll(" ", "_").replaceAll(" ", "_").equalsIgnoreCase(Player.getRSPlayer().getName().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										continue;
									}
									Scanner scan=new Scanner(s);
									if(scan.hasNextLine())
									{
										String chars=Files.readAllLines(Paths.get(MainPath+"/players/"+s.getName())).get(1);
										//println(chars);
										if(!chars.isEmpty())
										{
											characters=new Integer(chars.split(":")[1]);
											//println(characters);
											listed.add(characters);
											//println(listed);
											listedName.add(s.getName().substring(0, s.getName().length()-4));
											//println(listedName);
										}
									}
									scan.close();
								}
								//println(listed.size());
								for(int i=0;i<listed.size();i++)
								{
									if(listed.get(i)>third)
									{
										if(listed.get(i)>second)
										{
											if(listed.get(i)>first)
											{
												thirdName=secondName;
												secondName=firstName;
												firstName=listedName.get(i);
												third=second;
												second=first;
												first=listed.get(i);
												continue;
											}
											third=second;
											thirdName=secondName;
											second=listed.get(i);
											secondName=listedName.get(i);
											continue;
										}
										third=listed.get(i);
										thirdName=listedName.get(i);
									}
								}
								//Collections.sort(listed,Collections.reverseOrder());
								//println("after second loop");
								printMessage("[1: "+firstName+" = "+first+"][2: "+secondName+" = "+second+"][3: "+thirdName+" = "+third+"]",type);
								taskComplete=true;
								return;
							}
							catch(Exception e)
							{

								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
						}
						else if(parts.size()>=2 && parts.get(1).toLowerCase().equals("time"))
						{
							try{
								long first=0;
								long second=0;
								long third=0;
								File folder=new File(MainPath+"/playersData/");
								File[] files=folder.listFiles();
								String firstName="";
								String secondName="";
								String thirdName="";
								ArrayList<Long> listed=new ArrayList<Long>();
								ArrayList<String> listedName=new ArrayList<String>();
								listed.clear();
								listedName.clear();
								//println(files.length);
								for(File temp:files)
								{

									if(temp.getName().substring(0,temp.getName().length()-4).replaceAll(" ", "_").replaceAll(" ", "_").equalsIgnoreCase(Player.getRSPlayer().getName().replaceAll(" ", "_").replaceAll(" ", "_")))
									{
										continue;
									}
									Scanner scan=new Scanner(temp);
									if(scan.hasNextLine())
									{
										String timeSpent=Files.readAllLines(Paths.get(MainPath+"/playersData/"+temp.getName())).get(1);
										//println(chars);
										if(!timeSpent.isEmpty())
										{
											Matcher match=Pattern.compile(":.*").matcher(timeSpent);
											if(match.find())
											{
												//println(temp.getName().substring(0, temp.getName().length()-4)+" :: "+Long.valueOf(match.group(0).replaceFirst(":", "")));
												listed.add(Long.valueOf(match.group(0).replaceFirst(":", "")));
												listedName.add(temp.getName().substring(0, temp.getName().length()-4));
											}
										}
									}
									scan.close();
								}
								//println(listed.size());
								//println(listed);
								for(int i=0;i<listed.size();i++)
								{
									if(listed.get(i)>third)
									{
										if(listed.get(i)>second)
										{
											if(listed.get(i)>first)
											{
												thirdName=secondName;
												secondName=firstName;
												firstName=listedName.get(i);
												third=second;
												second=first;
												first=listed.get(i);
												continue;
											}
											third=second;
											thirdName=secondName;
											second=listed.get(i);
											secondName=listedName.get(i);
											continue;
										}
										third=listed.get(i);
										thirdName=listedName.get(i);
									}
								}
								BigDecimal firstBD=new BigDecimal(String.valueOf(Double.valueOf(first)/3600000)).setScale(2, BigDecimal.ROUND_HALF_UP);
								BigDecimal secondBD=new BigDecimal(String.valueOf(Double.valueOf(second)/3600000)).setScale(2, BigDecimal.ROUND_HALF_UP);
								BigDecimal thirdBD=new BigDecimal(String.valueOf(Double.valueOf(third)/3600000)).setScale(2, BigDecimal.ROUND_HALF_UP);
								printMessage("[1: "+firstName+" = "+firstBD+" hours][2: "+secondName+" = "+secondBD+" hours][3: "+thirdName+" = "+thirdBD+" hours]",type);
								taskComplete=true;
								return;
							}
							catch(Exception e)
							{

								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
						}
						else{
							Logging("invalid parameters for top command",type);
							printMessage("type !!help top for valid parameters",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("botloc"))
					{
						printMessage("My coordinates are: [X: "+Player.getPosition().getX()+"] [Y: "+Player.getPosition().getY()+"] [Plane: "+Player.getPosition().getPlane()+"]",type);
						taskComplete=true;
						return;
					}
					else if(parts.get(0).toLowerCase().equals("examine"))
					{
						Logging("Examine command detected",type);

						boolean itemFound=false;
						if(parts.size()>=2)
						{
							/*String item="";
							for(int i=1;i<parts.size();i++)
							{
								if(parts.size()-1==i)
								{
									item+=parts.get(i);
								}
								else{
									item+=parts.get(i)+" ";
								}
							}*/
							File folder=new File(MainPath+"/ItemDB/");
							File[] ItemDB=folder.listFiles();
							String examine="";
							for(File s:ItemDB)
							{
								if(s.isFile())
								{
									String itemName=s.getName().substring(0,s.getName().length()-4).toLowerCase();
									if(itemName.equals(item.toLowerCase()))
									{

										itemFound=true;
										Scanner scan;
										try {
											Logging("Scanning for ID of item",type);
											scan = new Scanner(s);
											String Fileline=scan.nextLine();
											Matcher matcher=Pattern.compile("\"description\":\"([\\w\\s?.,!?'-]*)").matcher(Fileline);
											scan.close();

											if(matcher.find())
											{
												try{
													examine=new String(matcher.group(1));
												}
												catch(NumberFormatException e){
													println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

												}
											}
											if(examine.length()<1)
											{
												Logging("Null string for examine",type);
												println("Null string");
												examine="Nothing to examine for this item.";
											}
										}
										catch (Exception e) {
											println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

										}
										Logging("Printing item examine property",type);
										printMessage("\""+examine+"\"",type);
										taskComplete=true;
										return;
									}
								}
							}
							if(!itemFound)
							{
								printMessage("Nothing interesting happens.",type);
								taskComplete=true;
								return;
							}
						}
					}
					else if(parts.get(0).toLowerCase().equals("price"))
					{
						Logging("Price command detected",type);
						boolean itemFound=false;
						if(parts.size() >=2)
						{

							/*String item="";
							for(int i=1;i<parts.size();i++)
							{
								if(parts.size()-1==i)
								{
									item+=parts.get(i);
								}
								else{
									item+=parts.get(i)+" ";
								}
							}*/
							//
							File folder=new File(MainPath+"/ItemDB/");
							File[] ItemDB=folder.listFiles();
							String OSBPrice="0";
							String GEPrice="0";
							//println(item);
							double numericAmount=-1;
							boolean specificAmount=false;
							if(Character.isDigit(item.charAt(0)))
							{
								Matcher amount=Pattern.compile(".*?(?=\\.|\\s)").matcher(item);
								if(amount.find())
								{
									try{
										numericAmount=Long.parseLong(amount.group(0).replaceAll(" ", "").replaceAll(",", ""));
										item=item.substring(amount.group(0).length(), item.length()).replaceAll(" ", "");
										if(item.startsWith(" "))
										{
											item=item.replaceFirst(" ", "");
										}
										specificAmount=true;
									}
									catch(Exception e)
									{
										println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
									}
								}
							}
							//println("test1");
							for(File s:ItemDB)
							{
								if(s.isFile())
								{
									String itemName=s.getName().substring(0,s.getName().length()-4).toLowerCase().replaceAll(" ", "");
									if(itemName.equals(item.toLowerCase().replaceAll(" ", "")))
									{
										//println(item);
										itemFound=true;
										Scanner scan;
										try {
											Logging("Scanning for ID of item",type);
											scan = new Scanner(s);
											String Fileline=scan.nextLine();
											Matcher matcher=Pattern.compile("\"id\":([\\d]*)").matcher(Fileline);
											scan.close();

											Integer id=0;
											if(matcher.find())
											{
												try{
													id=new Integer(matcher.group(1));
												}
												catch(NumberFormatException e){
													println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

												}
											}
											Logging("Getting price data for urls",type);

											URL GE=new URL("http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item="+id);
											URL OSB=new URL("http://api.rsbuddy.com/grandExchange?a=guidePrice&i="+id);
											InputStreamReader inOSB=null;
											InputStreamReader inGE=null;
											try{
												inGE = new InputStreamReader(GE.openStream());
												inOSB = new InputStreamReader(OSB.openStream());
											}
											catch(Exception f)
											{
												println(f.toString()); Logging(f.toString(),type); for(int z=0;z<f.getStackTrace().length;z++) { println(f.getStackTrace()[z]); Logging(f.getStackTrace()[z].toString(),type); }	
											}
											StringBuffer siteLineOSB=new StringBuffer();
											StringBuffer siteLineGE = new StringBuffer();
											if(inGE!=null)
											{
												int c = inGE.read();
												while (c != -1 && c != '\n') {
													siteLineGE.append((char)c);
													c = inGE.read();
												}
											}
											if(inOSB!=null)
											{
												int a = inOSB.read();
												while (a != -1 && a != '\n') {
													siteLineOSB.append((char)a);
													a = inOSB.read();
												}
											}
											String lineConvertedGE=siteLineGE.toString();
											String lineConvertedOSB=siteLineOSB.toString();

											if(lineConvertedGE.length()<1)
											{
												println("null GE");
												GEPrice="Unavailable";
											}
											else
											{

												Matcher matcherGE=Pattern.compile("neutral\",\"price\":\"(\\d*\\W?\\d*\\w?)").matcher(lineConvertedGE);
												if(matcherGE.find())
												{
													GEPrice=matcherGE.group(1).replaceAll("\"","");
												}


											}
											if(lineConvertedOSB.length()<1)
											{
												println("null OSB");
												OSBPrice="Unavailable";
											}
											else
											{
												Matcher matcherOSB=Pattern.compile("\"selling\":(\\d*)").matcher(lineConvertedOSB);
												if(matcherOSB.find())
												{
													OSBPrice=NumberFormat.getInstance().format(Long.parseLong(matcherOSB.group(1)));
												}

											}
										} catch (Exception e) {
											println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

										}
										Logging("Printing item prices",type);
										//println("test2");
										if(specificAmount)
										{
											double GEPriceC=0;
											if(GEPrice.toLowerCase().contains("m"))
											{
												//println("test4");
												GEPriceC=Double.parseDouble(GEPrice.replaceAll("m", "").replaceAll(",", ""))*1000000;
											}
											else if(GEPrice.toLowerCase().contains("k"))
											{
												GEPriceC=Double.parseDouble(GEPrice.replaceAll("k", "").replaceAll(",", ""))*1000;

											}
											else
											{
												//println("test5");
												//println(GEPrice);
												GEPriceC=Double.parseDouble(GEPrice.replaceAll(",", ""));
											}
											//println("test3");
											try{
												printMessage("[G.E] "+NumberFormat.getInstance().format(((long) GEPriceC)*(long) numericAmount)+"("+NumberFormat.getInstance().format((long) GEPriceC)+")"+" [OSB] "+NumberFormat.getInstance().format(Long.parseLong(OSBPrice.replaceAll(",", ""))*(long) numericAmount)+"("+OSBPrice+")",type);
											}catch(Exception e)
											{
												println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
											}

										}
										else{
											printMessage("[G.E] "+GEPrice+" [OSB] "+OSBPrice,type);
										}
										taskComplete=true;
										return;
									}
								}
							}
							if(!itemFound)
							{
								println("Item not found");
								Logging("Could not find item for price checking",type);
								printMessage("Couldn't find item",type);
								taskComplete=true;
								return;
							}
						}
						else
						{
							Logging("Not enough parameters for price checking",type);
							printMessage("Enter a valid item",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("help"))
					{
						Logging("Help command detected",type);
						//println("Test");
						if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("data"))
						{
							Logging("Data help command",type);
							printMessage("Parameters: [rank/joindate/days/chars/words/time] [player_name/blank if yourself]",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("events")){
							Logging("Events help command",type);
							printMessage("Parameters: [add/remove/leave blank] [event_name] [time till in mins] [details]",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("examine")){
							Logging("Examine help command",type);
							printMessage("Parameters: [item name] it will return the examine text of a tradeable object.",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("time")){
							Logging("Time help command",type);
							printMessage("Parameters: [comp/leave blank for time/timezone]",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("joke")){
							Logging("Joke help command",type);
							printMessage("Provides you with a bad joke.",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("top")){
							Logging("Top help command",type);
							printMessage("Parameters: [comp/time/words/chars/comprank]",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("8ball")){
							Logging("8ball help command",type);
							printMessage("Parameters: [question]? (Must end in question mark)",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("levelup")){
							Logging("Levelup help command",type);
							printMessage("Parameters: [level] [skill]",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("screenie")){
							Logging("Screenie help command",type);
							printMessage("Useful for evidence taking when gold stars aren't around. Saves image locally for xmax to see later.",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("price")){
							Logging("Price help command",type);
							printMessage("Enter the item name exactly as it is in-game. E.G: !!price abyssal whip",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("poll")){
							Logging("Poll help command",type);
							printMessage("runs a poll for 15 seconds, 'y' or 'n' answers only.",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("calc")){
							Logging("Calculator command",type);
							printMessage("Performs calculations: !!calc(50*20.5)/sin(50)",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("cs")){
							Logging("Clue Solver command",type);
							printMessage("Parameters: [anagram/cipher/cryptic] [anagram/cipher/Any part of cryptic clue]",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("lastseen")){
							Logging("Last seen command help command",type);
							printMessage("Parameters: [player_name]",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("flipcoin")){
							Logging("Last seen command help command",type);
							printMessage("Flips the coin.",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("edit")){
							Logging("Last seen command help command",type);
							printMessage("Parameters: [examine/name] [\"examine text\"/old_name]",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("cml")){
							Logging("Last seen command help command",type);
							printMessage("!!cml [skill] [7d5h30m15s time format] [optional_name/leave blank if yourself]",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("comppoll")){
							Logging("Last seen command help command",type);
							printMessage("!!comppoll vote [skill_name] or leave eveyrthing blank to get current results ",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("fact")){
							Logging("Last seen command help command",type);
							printMessage("!!fact [optional specific type] types available: trivia, math, date, year. Otherwise will pick a random one.",type);
							taskComplete=true;
							return;
						}
						else if(parts.size()>=2&&parts.get(1) != null && parts.get(1).toLowerCase().equals("compwins")){
							Logging("Last seen command help command",type);
							printMessage("!!compwins [update/dpfund/player_name] will use competition winners data sheet.",type);
							taskComplete=true;
							return;
						}
						else{
							Logging("List all commands help",type);
							printMessage(": data time top help 8ball levelup screenie price joke offsite bot poll calc cs examine lastseen flipcoin comppoll fact compwins",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("8ball"))
					{
						Logging("8ball command detected",type);
						if(parts.size()>=2 && parts.get(parts.size()-1).endsWith("?"))
						{
							if(parts.get(1).startsWith(" "))
							{
								Logging("Empty question",type);
								printMessage("ask a proper question noob",type);
								taskComplete=true;
								return;
							}
							else{
								Logging("Generating answer for 8ball",type);
								printMessage(ballAnswers[General.random(0, ballAnswers.length-1)],type);
								taskComplete=true;
								return;
							}
						}
						else{
							Logging("No question mark at the end - 8ball",type);
							printMessage("I don't see a question...",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("offsite"))
					{
						Logging("off-site command detected",type);
						printMessage("ossociety—.—o—r—g is our clan's official website!",type);
						taskComplete=true;
						return;
					}
					else if(parts.get(0).toLowerCase().equals("caps") && (!player_rank.equalsIgnoreCase("2 banana") || !player_rank.equalsIgnoreCase("1 banana")))
					{
						if(parts.size() >= 2)
						{
							String original = originalText.toUpperCase();
							String allCaps = "";
							for(int length = 0; length < original.length(); length++)
							{
								if(Character.isSpaceChar(original.charAt(length)))
								{
									allCaps += " ";
									continue;
								}
								allCaps += String.valueOf(original.charAt(length)) + "—";
							}
							printMessage(allCaps,type);
							taskComplete=true;
							return;
						}

					}
					else if(parts.get(0).toLowerCase().equals("calc"))
					{
						if(parts.size()>=2)
						{
							if(formatted2[1].equalsIgnoreCase(" 9+10")||formatted2[1].equalsIgnoreCase((" 10+9")))
							{
								printMessage("21",type);
								taskComplete=true;
								return;
							}
							Logging("Calculation command",type);
							printMessage(String.valueOf(NumberFormat.getInstance().format(eval(formatted2[1]))),type);
							taskComplete=true;
							return;
						}
						else{
							printMessage("Nothing to calculate was found",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("bot"))
					{
						if(parts.size() == 2 && parts.get(1).equalsIgnoreCase("create") && name.equalsIgnoreCase("xmax"))
						{
							try
							{
								URL url = new URL("https://cleverbot.io/1.0/create");
								URLConnection con = url.openConnection();
								HttpURLConnection http = (HttpURLConnection)con;
								http.setRequestMethod("POST"); // PUT is another valid option
								http.setDoOutput(true);
								byte[] out = "{\"user\":\"K9dRnCH3Ym2hivt6\",\"key\":\"8NO2ywN85RyAnSl03TKNopzAX9o9hoW9\",\"nick\":\"OSS Bot\"}" .getBytes(StandardCharsets.UTF_8);
								int length = out.length;
								http.setFixedLengthStreamingMode(length);
								http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
								http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
								http.connect();
								try(OutputStream os = http.getOutputStream()) {
									os.write(out);
								}
								Reader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
								StringBuilder sb = new StringBuilder();
								for (int c; (c = in.read()) >= 0;)
									sb.append((char)c);
								String response = sb.toString();
								Matcher m = Pattern.compile("\"status\":\"(\\w*)").matcher(response);
								if(m.find() && m.groupCount() >= 1)
								{
									if(m.group(1).equalsIgnoreCase("success"))
									{
										printMessage("Bot instance created.",type);
									}
									else
									{
										printMessage("Bot instance creation failed",type);
									}
								}
							}catch(Exception e)
							{
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
						}
						else
						{
							try{
								String query = command.substring(6);
								//General.println(query);
								URL url = new URL("https://cleverbot.io/1.0/ask");
								URLConnection con = url.openConnection();
								HttpURLConnection http = (HttpURLConnection)con;
								http.setRequestMethod("POST"); // PUT is another valid option
								http.setDoOutput(true);
								byte[] out = ("{\"user\":\"K9dRnCH3Ym2hivt6\",\"key\":\"8NO2ywN85RyAnSl03TKNopzAX9o9hoW9\",\"nick\":\"OSS Bot\",\"text\":\"" + query + "\"}").getBytes(StandardCharsets.UTF_8);
								int length = out.length;
								http.setFixedLengthStreamingMode(length);
								http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
								http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
								http.connect();
								try(OutputStream os = http.getOutputStream()) {
									os.write(out);
								}
								Reader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
								StringBuilder sb = new StringBuilder();
								for (int c; (c = in.read()) >= 0;)
									sb.append((char)c);
								String response = sb.toString();
								Matcher m = Pattern.compile("\"status\":\"(\\w*)").matcher(response);
								if(m.find() && m.groupCount() >= 1)
								{
									if(m.group(1).equalsIgnoreCase("success"))
									{
										Matcher m2 = Pattern.compile("\"response\":\"(.*)\"\\}").matcher(response);
										if(m2.find() && m2.groupCount() >= 1)
										{
											printMessage(m2.group(1),type);
										}
									}
									else
									{
										printMessage("Bot query unsuccessful.",type);
									}
								}
								else
								{
									printMessage("Bot query unsuccessful.",type);
								}
							}
							catch(Exception e)
							{
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
						}
						taskComplete=true;
						return;
					}
					else if(parts.get(0).toLowerCase().equals("levelup")){
						Logging("Levelup command detected",type);
						if(parts.size()>=3)
						{
							Integer level=0;
							try{
								level=new Integer(parts.get(1));
							}
							catch(NumberFormatException e){

								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
							if(level <= 2277 && level >0)
							{

								String s="";
								for(int i=1;i<parts.size();i++)
								{
									if(parts.size()-1==i)
									{
										s+=parts.get(i);
									}
									else{
										s+=parts.get(i)+" ";
									}
								}
								int rand=1;
								while(rand%2 != 0)
								{
									rand=General.random(2, congratsMessages.length);
								}
								Logging("Generate and pring congratulations message for levelup command",type);
								if(name.equalsIgnoreCase("brandilyn") && parts.get(1).equals("99") && parts.get(2).equalsIgnoreCase("mining"))
								{
									printMessage("About fucking time jesus fuck.",type);
									taskComplete=true;
									return;
								}
								printMessage(congratsMessages[rand-2]+s+congratsMessages[rand-1],type);
								taskComplete=true;
								return;
							}
							else
							{
								Logging("Number out of valid range - levelup",type);
								printMessage("No such thing as having a level of "+level,type);
								taskComplete=true;
								return;
							}
						}
						else
						{
							Logging("Invalid parameters for levelup command",type);
							printMessage("Invalid parameters noob. !!levelup [level] [skill] ",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("xmax")){
						{
							try{
								if(Interfaces.get(7).isValid() && Interfaces.get(7).getChild(14).getChildren().length >0)
								{
									//println("test3");
									//println(Interfaces.get(589).getChild(5).getChildren().length);
									for(int i=0;i<Interfaces.get(7).getChild(14).getChildren().length;i+=5)
									{
										//println("test4");
										String currentPlayer=Interfaces.get(7,14).getChild(i).getText().replace("<img=2>", "").replace("<img=0>", "").replace("<img=1>", "").replaceAll(" ", " ").replaceAll(" ", " ");
										//println(currentPlayer);
										if(!currentPlayer.equals("xmax"))
										{
											//println("True");
											printMessage("He's currently in the cc you nab.",type);
											taskComplete=true;
											return;
										}
									}
								}
							}
							catch(Exception e)
							{
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

							}
							printMessage("He's currently not in the cc :(",type);
							taskComplete=true;
							return;
						}
					}
					/*else if(parts.get(0).toLowerCase().equals("lostrank"))
					{
						Logging("Lostrank command detected",type);
						Screenshots.take("Lost_ranks/"+name+fileFormat.format(System.currentTimeMillis())+".png", 	false, false);
						printMessage("Noted.",type);
						taskComplete=true;
						return;
					}*/
					else if(parts.get(0).toLowerCase().equals("screenie"))
					{
						Logging("Screenie comand detected",type);
						SimpleDateFormat fileFormat=new SimpleDateFormat("MMM d HH-mm-ss z");
						fileFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
						Screenshots.take(name+" - "+fileFormat.format(System.currentTimeMillis())+".png", false, false);
						taskComplete=true;
						return;
					}
					else if(parts.get(0).toLowerCase().equals("joke"))
					{	
						Logging("Joke command detected",type);
						printMessage(jokes[General.random(0, jokes.length-1)],type);
						taskComplete=true;
						return;
					}
					else if(parts.get(0).toLowerCase().equals("poll"))
					{	
						try{
							if(parts.size() >= 2 && parts.get(1).equalsIgnoreCase("quick"))
							{
								String s=command.substring(13, command.length());
								Logging("poll command detected",type);
								if(s.length()>=1)
								{
									long start=System.currentTimeMillis();
									pollRunning=true;
									Logging("poll started",type);
									printMessage("Poll: | "+s+" || Y or N",true);
									while(start+30000 >= System.currentTimeMillis())
									{
										sleep(20);
									}
									int yes=0,no=0;
									if(votes.size()>=1)
									{
										for(int i=0;i<votes.size();i++)
										{
											//println(votes.get(i));
											if(votes.get(i).equals("y"))
											{
												//println("true for yes");
												yes++;
											}
											else if(votes.get(i).equals("n")){
												//println("true for no");
												no++;
											}
										}
									}
									voters.clear();
									votes.clear();
									Logging("poll ended",type);
									printMessage("poll ended: Yes: "+yes+" || No: "+no,true);
									pollRunning=false;
									taskComplete=true;
									return;
								}
								else{
									Logging("Poll empty string",type);
									printMessage("Must type something to poll first",true);
									taskComplete=true;
									return;
								}
							}
							else if(parts.size() >= 4 && parts.get(1).equalsIgnoreCase("create"))
							{
								if(command.contains("::"))
								{
									String toDealWith = command.substring(14,command.length());
									Matcher split = Pattern.compile("(.*)::(.*)").matcher(toDealWith);
									if(split.find())
									{
										if(split.groupCount() == 2)
										{
											String toPoll = split.group(1);
											if(toPoll.length() <= 3)
											{
												printMessage("Not a valid name for a poll.",type);
												taskComplete=true;
												return;
											}
											String options = split.group(2);
											int noOfOptions = options.split("/").length;
											if(noOfOptions == 0 || noOfOptions > 10)
											{
												printMessage("Not a valid number of voting options.",type);
												taskComplete=true;
												return;
											}
											File pollFolder = new File(MainPath + "/polls/");
											File[] pollFiles = pollFolder.listFiles();
											int pollCounter = 0;
											for(File pollFile : pollFiles)
											{
												int fileNo = 0;
												String fName = pollFile.getName().replaceAll(".txt", "");
												fileNo = Integer.parseInt(fName);
												if(fileNo > pollCounter)
												{
													pollCounter = fileNo;
												}
											}
											pollCounter++;

											File pollFile = new File(MainPath + "/polls/", pollCounter + ".txt");
											pollFile.createNewFile();

											FileWriter fw = new FileWriter(pollFile,false);
											fw.write(toPoll + System.lineSeparator() + name + System.lineSeparator() + options);
											fw.close();
											printMessage("Poll successfully created. —I—D—: " + pollCounter,type);
											taskComplete=true;
											return;
										}
									}
								}
							}
							else if(parts.size() >= 4 && parts.get(1).equalsIgnoreCase("vote"))
							{
								int id = Integer.parseInt(parts.get(2));
								int vote = Integer.parseInt(parts.get(3));
								File voteFile = new File(MainPath  + "/polls/", id + ".txt");
								Scanner vScan = new Scanner(voteFile);
								int tempCounter = 0;
								while(vScan.hasNextLine())
								{
									String line = vScan.nextLine();
									if(tempCounter >= 3)
									{
										String[] lineSplit = line.split(":");
										if(lineSplit[0].equalsIgnoreCase(name))
										{
											printMessage("You've already voted for this, "+name, type);
											vScan.close();
											taskComplete=true;
											return;
										}
									}
									tempCounter++;
								}
								vScan.close();
								String[] options  = Files.readAllLines(Paths.get(MainPath + "/polls/" + id + ".txt")).get(2).split("/");
								if(options.length > 0)
								{
									for(int i = 0; i<options.length; i++)
									{
										if((i+1) == vote)
										{
											FileWriter fw = new FileWriter(voteFile,true);
											fw.write(System.lineSeparator() + name + ":" + options[i]);
											fw.close();
											printMessage("Vote successfully submitted by: " + name + " for '" + options[i] + "'",type);
											taskComplete=true;
											return;
										}
									}
									String finalMsg = "";
									for(int i = 0; i <options.length; i++)
									{
										if(options.length == (i+1))
										{
											finalMsg += (i+1) + ": " + options[i];
											break;
										}
										finalMsg += (i+1) + ": " + options[i] + " | ";
									}
									printMessage(finalMsg,type);
									taskComplete=true;
									return;
								}
							}
							else if(parts.size() == 3 && parts.get(1).equalsIgnoreCase("delete"))
							{
								int pollNo = Integer.valueOf(parts.get(2));
								File[] voteFiles = new File(MainPath + "/polls/").listFiles();

								for(File voteFile : voteFiles)
								{
									String fName = voteFile.getName().replaceAll(".txt", "");
									int voteFileNo = Integer.valueOf(fName);
									if(voteFileNo == pollNo)
									{
										if(player_rank.equalsIgnoreCase("gold") || player_rank.equalsIgnoreCase("silver"))
										{
											voteFile.delete();
											printMessage("Poll of —I—D—: " + fName + " has been deleted.",type);
											taskComplete=true;
											return;
										}
										Scanner s = new Scanner(voteFile);
										int tempCounter = 0;
										while(s.hasNextLine())
										{
											String line = s.nextLine();
											if(tempCounter == 1)
											{
												if(line.equalsIgnoreCase(name))
												{
													voteFile.delete();
													printMessage("Poll of —I—D—: " + fName + " has been deleted.",type);
													s.close();
													taskComplete=true;
													return;
												}
											}
											tempCounter++;
										}
										s.close();
										printMessage("You're not allowed to delete this poll.",type);
										taskComplete=true;
										return;
									}
								}
							}
							else if(parts.size() == 2 && parts.get(1).equalsIgnoreCase("list"))
							{
								File[] polls = new File(MainPath + "/polls/").listFiles();
								String finalMsg = "";
								for(File poll : polls)
								{
									String fName = poll.getName().replaceAll(".txt", "");
									finalMsg += fName + ",";
								}
								finalMsg = finalMsg.substring(0, finalMsg.length()-1);
								printMessage("Poll —I—Ds: " + finalMsg,type);
								taskComplete=true;
								return;
							}
							else if(parts.size() == 2)
							{
								int voteFile = Integer.valueOf(parts.get(1));
								File dr = new File(MainPath + "/polls/");
								File[] allFiles = dr.listFiles();
								for(File file : allFiles)
								{
									String fName = file.getName().replaceAll(".txt", "");
									if(Integer.valueOf(fName) == voteFile)
									{
										Scanner s = new Scanner(file);
										int tempCounter = 0;
										List<String> options = new ArrayList<String>();
										List<String> votes = new ArrayList<String>();
										List<Integer> VC = new ArrayList<Integer>();
										while(s.hasNextLine())
										{
											String line = s.nextLine();
											if(tempCounter == 2)
											{
												String[] opts = line.split("/");
												for(int i = 0; i <opts.length;i++)
												{
													options.add(opts[i]);
												}
											}
											else if(tempCounter >= 3)
											{
												String[] split = line.split(":");
												String vote = split[1];
												/*for(int i = 0; i < votes.size(); i++)
												{
													String exists = votes.get(i);
													String[] eVote = exists.split(":");
													if(eVote[0].equalsIgnoreCase(vote))
													{
														int prevCount = Integer.parseInt(eVote[1]);
														prevCount++;
														votes.set(i, eVote[0] + ":" + prevCount);
														break;
													}
												}*/
												votes.add(vote);
											}
											tempCounter++;
										}
										s.close();
										for(int i = 0; i < options.size(); i++)
										{
											VC.add(0);
										}
										for(String vote : votes)
										{
											for(int i = 0; i < options.size(); i++)
											{
												if(options.get(i).equalsIgnoreCase(vote))
												{
													VC.set(i, (VC.get(i)+1));
												}
											}
										}
										int totalVotes = votes.size();
										String finalMsg = "";
										DecimalFormat df = new DecimalFormat("0.##");
										for(int i = 0; i < options.size();i++)
										{
											int voteNo = VC.get(i);
											double percentage;
											if(totalVotes == 0)
											{
												percentage = 0;
											}
											else
											{
												percentage = Double.parseDouble(df.format((voteNo/totalVotes)*100));
											}
											if(options.size() == (i+1))
											{
												finalMsg += "[" + (i+1) + "]" + options.get(i) + ": " + percentage + "%/" + voteNo; 
												break;
											}
											finalMsg += "[" + (i+1) + "]" + options.get(i) + ": " + percentage + "%/" + voteNo +" | "; 
										}
										printMessage(finalMsg,type);
										taskComplete=true;
										return;
									}
								}
								printMessage("Poll —I—D not found.",type);
								taskComplete=true;
								return;
							}
						}
						catch(Exception e)
						{
							println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

						}
					}
					else if(parts.get(0).toLowerCase().equals("edit"))
					{
						//println("1");
						if(parts.size()>=4&&parts.get(1).toLowerCase().equals("examine") && (player_rank.equalsIgnoreCase("key") || player_rank.equalsIgnoreCase("gold") || player_rank.equalsIgnoreCase("silver")))
						{
							//println("2");
							//println(editText);
							Matcher matcher2=Pattern.compile("(.*?)\"").matcher(editText);
							if(matcher2.find())
							{
								//println("3");
								if(matcher2.group(1).length()>=2)
								{
									int occurance=editText.length()-editText.replaceAll(" ", "").length();
									if(occurance>=1)
									{
										//println("4");
										Matcher matcher=Pattern.compile("\"(.*?)\"").matcher(editText);
										if(matcher.find())
										{	

											if(updateExamine(matcher2.group(1).trim(),matcher.group(1)))
											{
												printMessage("Updated.",type);
												taskComplete=true;
												return;
											}
											else
											{
												printMessage("Failed to update",type);
												taskComplete=true;
												return;
											}
										}
									}
								}
								else
								{
									printMessage("Failed to update",type);
									taskComplete=true;
									return;
								}
							}
						}
						else if(parts.size()>=3&& command.length()>=13)
						{
							println("0");
							String result=verifyName(name,command.substring(12));
							pauseLoginData=false;
							if(result==null)
							{
								printMessage("Unexpected error happened",type);
								taskComplete=true;
								return;
							}
							else if(result.length()==3)
							{
								printMessage("Login data: ["+result.substring(0, 1)+"] Character data: ["+result.substring(1, 2)+"] Time data: ["+result.substring(2, 3)+"]",type);
								taskComplete=true;
								return;
							}
							else if(result.length()>=4)
							{
								printMessage("Error: "+result,type);
								taskComplete=true;
								return;
							}
						}
						else
						{
							printMessage("Parameters: examine/name object name \"examine text within quotes!\"",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("lastseen"))
					{	
						Logging("Last seen command",type);
						if(parts.size() == 2 && parts.get(1).equalsIgnoreCase("literallyeveryone") && (player_rank.equalsIgnoreCase("gold") || player_rank.equalsIgnoreCase("key")))
						{
							try{
								File lastseenFile = new File(MainPath+"/lastseens.txt");
								if(lastseenFile.exists())
								{
									lastseenFile.delete();
								}
								lastseenFile.createNewFile();
								FileWriter fw = new FileWriter(lastseenFile,true);
								for(int i=0; i<list.size();i+=4)
								{
									//General.println(i);
									String player = list.get(i).replaceAll(" ", "_").replaceAll(" ", "_");
									int rankNo = 0;
									if(!list.get(i+1).equals(""))
									{
										rankNo = Integer.parseInt(list.get(i+1));
									}
									if(rankNo != 0)
									{

										File[] dir = new File(MainPath+"/playersData/").listFiles();
										for(File pFile:dir)
										{

											String fileName = pFile.getName().replaceAll(".txt", "").replaceAll(" ", "_").replaceAll(" ", "_");
											if(fileName.equalsIgnoreCase(player))
											{
												//General.println(player);
												List<String> data = new ArrayList<String>();
												Scanner s = new Scanner(pFile);
												while(s.hasNextLine())
												{
													data.add(s.nextLine());
												}
												s.close();
												if(data.size() >= 2)
												{
													//General.println("4");
													String[] split = data.get(0).split(":");
													if(split[0].equalsIgnoreCase("lastseen"))
													{
														long lastseen = Long.parseLong(split[1]);
														//General.println(lastseen);
														if(System.currentTimeMillis()-lastseen >= 864000000L)
														{
															long diff=System.currentTimeMillis()-lastseen;
															//General.println(diff);
															double daysDiff = Double.parseDouble(DecimalFormat.getInstance().format(diff/(1000.0*60.0*60.0*24.0)));
															fw.write(player+":"+daysDiff + System.lineSeparator());
															break;
														}
													}
												}
											}
										}
									}
								}
								fw.close();
								printMessage("Everyone with 10+ days inactivity has been marked.",type);
								taskComplete=true;
								return;
							}
							catch(Exception e)
							{
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

							}

						}
						else if(parts.size()>=2)
						{
							String pname="";
							for(int i=1;i<parts.size();i++)
							{
								if(parts.size()-1==i)
								{
									pname+=parts.get(i);
								}
								else{
									pname+=parts.get(i)+" ";
								}
							}
							pname=pname.toLowerCase().replaceAll(" ", "_");
							try{

								File folder=new File(MainPath+"/playersData/");
								File[] playerDB=folder.listFiles();
								String lastseen="";
								String realName="";
								//String timeSpent="";
								boolean playerFound=false;
								for(File temp:playerDB)
								{
									if(temp.getName().substring(0,temp.getName().length()-4).replaceAll(" ", "_").equalsIgnoreCase(pname))
									{
										realName=temp.getName().substring(0,temp.getName().length()-4);
										playerFound=true;
										Scanner scan=new Scanner(temp);
										while(scan.hasNextLine())
										{
											lastseen=scan.nextLine();
											break;
										}
										scan.close();
										break;
									}
								}
								if(Interfaces.get(7).isValid() && Interfaces.get(7,14).getChildren().length>0)
								{
									for(int i=0;i<Interfaces.get(7,14).getChildren().length;i+=5)
									{
										//println(i);
										if(realName.replace("<img=2>", "").replace("<img=0>", "").replace("<img=1>", "").replace("<img=3>", "").replaceAll(" ", " ").replaceAll(" ", " ").equalsIgnoreCase(Interfaces.get(7, 14).getChild(i).getText().replace("<img=2>", "").replace("<img=0>", "").replace("<img=1>", "").replace("<img=3>", "").replaceAll(" ", " ").replaceAll(" ", " ")))
										{
											printMessage(realName+" is currently in the clan chat.",type);
											taskComplete=true;
											return;
										}
									}
								}
								if(playerFound)
								{
									Matcher matcher=Pattern.compile(":.*").matcher(lastseen);
									//Matcher matcher2=Pattern.compile(":.*").matcher(timeSpent);
									Logging("Found player data on lastseen command",type);
									if(matcher.find())
									{
										//println(matcher.groupCount());
										SimpleDateFormat date=new SimpleDateFormat("MMM d, YYYY");
										SimpleDateFormat time=new SimpleDateFormat("HH:mm:ss");
										time.setTimeZone(TimeZone.getTimeZone("UTC"));
										date.setTimeZone(TimeZone.getTimeZone("UTC"));
										printMessage(realName+" was last seen on "+date.format(Long.valueOf(matcher.group(0).replaceFirst(":", "")))+" at "+time.format(Long.valueOf(matcher.group(0).replaceFirst(":", "")))+" UTC",type);
										taskComplete=true;
										return;
									}

								}
								else
								{
									Logging("Couldn't find player data lastseen command",type);
									printMessage("couldn't find player data.",type);
									taskComplete=true;
									return;
								}
							}
							catch(Exception e)
							{
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

							}
						}
						else{
							Logging("no name enetered in lastseen",type);
							printMessage("No name detected.",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("flipcoin"))
					{
						String[] coinFlip={"Heads.","Tails."};
						printMessage(coinFlip[General.random(0, 1)],type);
						taskComplete=true;
						return;
					}
					else if(parts.get(0).toLowerCase().equals("cml"))
					{
						if(parts.size() >= 2 && parts.get(1).equalsIgnoreCase("update"))
						{
							try{
								name=name.replaceAll(" ", "_").replaceAll(" ", "_").toLowerCase();
								if(parts.size() >= 3)
								{
									name="";
									for(int i =2; parts.size() < i; i++)
									{
										if(i==parts.size()-1)
										{
											name+= parts.get(i);
											break;
										}
										name+=parts.get(i) + "_";
									}
								}
								URL url = new URL("http://crystalmathlabs.com/tracker/api.php?type=update&player="+name);
								URLConnection con = url.openConnection();
								HttpURLConnection http = (HttpURLConnection) con;
								http.setRequestMethod("GET");
								http.setDoOutput(true);
								http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
								http.connect();
								Reader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
								StringBuilder sb = new StringBuilder();
								for (int c; (c = in.read()) >= 0;)
									sb.append((char)c);
								String response = sb.toString();
								if(response.contains("1"))
								{
									printMessage(name + " has been updated successfully. Wait 30 seconds for update.",type);
								}
								else if(response.contains("2"))
								{
									printMessage(name + " wasn't found in the Runescape hiscores.",type);
								}
								else if(response.contains("3"))
								{
									printMessage("Negative xp gain detected for " + name,type);
								}
								else if(response.contains("4"))
								{
									printMessage("Unknown error occured when updating cml player: " + name,type);
								}
								else if(response.contains("5"))
								{
									printMessage(name + " has been updated already in the last 30 seconds.",type);
								}
								else if(response.contains("6"))
								{
									printMessage(name + " is in invalid name",type);
								}
								taskComplete=true;
								return;
							}
							catch(Exception e)
							{
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

							}
						}
						else if(parts.size() >= 3)
						{
							String pname="";
							if(parts.size() >=4)
							{
								for(int i=3;i<parts.size();i++)
								{
									if(i+1==parts.size())
									{
										pname+=parts.get(i);
										break;
									}
									pname+=parts.get(i)+" ";
								}
							}
							else
							{
								pname=name;
							}

							long seconds = convertToSeconds(parts.get(2));
							if(seconds <= 0)
							{
								printMessage("Not a valid time range",type);
								taskComplete=true;
								return;
							}
							if(getCMLData(pname, seconds, parts.get(1), type))
							{
								Logging("Goochi on cml command",type);
								taskComplete=true;
								return;
							}
							else
							{
								printMessage("Error occured while getting cml data",type);
								taskComplete=true;
								return;
							}
						}
						else
						{
							Logging("Invalid parameters for cml command",type);
							printMessage("!!cml [skill] [7d5h30m15s time format] [optional_name/leave blank if yourself]",type);
							taskComplete=true;
							return;
						}
					}
					else if(parts.get(0).toLowerCase().equals("comppoll"))
					{
						try{
							File votingFile = new File(MainPath,"poll.txt");
							Scanner s = new Scanner(votingFile);
							if(parts.size() >= 3 && parts.get(1).toLowerCase().equals("vote"))
							{
								String vote="";
								ArrayList<String> skillArray = new ArrayList<String>();
								int counter=0;

								while(s.hasNextLine())
								{

									String line = s.nextLine();
									if(counter < 3)
									{
										skillArray.add(line);
										if(line.toLowerCase().equalsIgnoreCase(parts.get(2)) && vote.isEmpty())
										{
											vote = line;
										}
										counter++;
										if(counter == 3 && vote.isEmpty())
										{
											printMessage("Valid options: "+skillArray.get(0)+" | " + skillArray.get(1) + " | " + skillArray.get(2),type);
											taskComplete=true;
											s.close();
											return;
										}
										continue;
									}

									String[] lineSplit = line.split(":");
									if(lineSplit[0].equalsIgnoreCase(name))
									{
										printMessage("You've already voted, "+name, type);
										s.close();
										taskComplete=true;
										return;
									}
								}
								s.close();
								FileWriter fw = new FileWriter(votingFile,true);
								fw.write(System.lineSeparator()+name+":"+vote);
								fw.close();
								printMessage("Vote successfully submitted by "+name,type);
								taskComplete=true;
								return;
							}
							else
							{
								int counter=0;
								ArrayList<String> possibleVotes = new ArrayList<String>();
								ArrayList<String> votes = new ArrayList<String>();
								while(s.hasNextLine())
								{
									if(counter < 3)
									{
										possibleVotes.add(s.nextLine());
										counter++;
										continue;
									}
									String line = s.nextLine();
									String[] lineSplit = line.split(":");
									votes.add(lineSplit[1]);
								}
								int skill1=0;
								int skill2=0;
								int skill3=0;
								for(String vote:votes)
								{
									if(possibleVotes.get(0).equalsIgnoreCase(vote))
									{
										skill1++;
									}
									else if(possibleVotes.get(1).equalsIgnoreCase(vote))
									{
										skill2++;
									}
									else if(possibleVotes.get(2).equalsIgnoreCase(vote))
									{
										skill3++;
									}
									else
									{
										General.println("Invalid skill vote?");
									}
								}
								double total=skill1+skill2+skill3;
								if(total==0)
								{
									total=1;
								}
								DecimalFormat df = new DecimalFormat("0.##");
								double percentage1 = Double.parseDouble(df.format((skill1/total)*100));
								double percentage2 = Double.parseDouble(df.format((skill2/total)*100));;
								double percentage3 = Double.parseDouble(df.format((skill3/total)*100));

								printMessage("["+possibleVotes.get(0)+"] "+ percentage1+"%/"+skill1+" || "+"["+possibleVotes.get(1)+"] "+ percentage2+"%/"+skill2+" || "+"["+possibleVotes.get(2)+"] "+ percentage3+"%/"+skill3,type);
								taskComplete=true;
								s.close();
								return;
							}
						}
						catch(Exception e)
						{

							println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
						}

					}
					else if(parts.get(0).toLowerCase().equalsIgnoreCase("fact"))
					{
						if((System.currentTimeMillis() - runTime) <= 15000 )
						{
							taskComplete=true;
							return;
						}
						runTime = System.currentTimeMillis();
						try {
							String[] options = {"trivia","math","date","year"};
							int random = General.random(0, 3);
							if(parts.size() == 2)
							{
								if(parts.get(1).equalsIgnoreCase("trivia"))
								{
									random = 0;
								}
								else if(parts.get(1).equalsIgnoreCase("math"))
								{
									random = 1;
								}
								else if(parts.get(1).equalsIgnoreCase("date"))
								{
									random = 2;
								}
								else if(parts.get(1).equalsIgnoreCase("year"))
								{
									random = 3;
								}
							}
							URL url = new URL("http://numbersapi.com/random/" + options[random]);
							URLConnection connection= url.openConnection();
							connection.setRequestProperty("Accept-Charset", java.nio.charset.StandardCharsets.UTF_8.name());
							connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
							connection.connect();

							BufferedReader factReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							String line = "";
							line = factReader.readLine();

							printMessage(line,type);
							taskComplete=true;
							return;
						} catch (IOException e) {
							println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
						}
					}
					else if(parts.get(0).toLowerCase().equalsIgnoreCase("compwins"))
					{
						if(parts.size() >= 2)
						{
							name = "";
							for(int i = 1;i<parts.size();i++)
							{
								if(parts.size() == i+1)
								{
									name += parts.get(i);
									break;
								}
								name+=parts.get(i) + " ";
							}
						}
						name = name.replaceAll(" ", "_");
						if(parts.size() == 2 && (player_rank.equalsIgnoreCase("gold") || player_rank.equalsIgnoreCase("key") || player_rank.equalsIgnoreCase("silver")) && parts.get(1).equalsIgnoreCase("update"))
						{
							try {

								URL winnersURL = new URL("https://docs.google.com/spreadsheets/d/1EKvjECjTnwwT9uJzMSu13UN3sYCF-yQaFFyFXUabZhg/pub?gid=1099814919&single=true&output=csv");
								URLConnection conn = winnersURL.openConnection();
								conn.setRequestProperty("Accept-Charset", java.nio.charset.StandardCharsets.UTF_8.name());
								conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
								conn.connect();

								BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
								String line = "";
								File winnersFile = new File(MainPath+"/winners.txt");
								if(!winnersFile.exists())
								{
									winnersFile.createNewFile();
								}
								FileWriter fw = new FileWriter(winnersFile,false);
								fw.write("");
								fw.close();
								fw = new FileWriter(winnersFile,true);
								while((line = reader.readLine()) != null)
								{
									fw.write(line + System.lineSeparator());
								}
								fw.close();
								printMessage("Successfully updated competition winners data", type);
								taskComplete=true;
								return;

							} catch (Exception e) {
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
						}
						else
						{
							File winnersFile = new File("winners.txt");
							List<WeekInfo> data = new ArrayList<WeekInfo>();
							try {
								Scanner scanner = new Scanner(winnersFile);
								while(scanner.hasNextLine())
								{
									String line = scanner.nextLine();
									String line2 = scanner.nextLine();
									String line3 = scanner.nextLine();
									Matcher regex = Pattern.compile("(.*),\"(.*)\",([yn])").matcher(line);
									Matcher regex2 = Pattern.compile("(.*),\"(.*)\",([yn])").matcher(line2);
									Matcher regex3 = Pattern.compile("(.*),\"(.*)\",([yn])").matcher(line3);
									if(regex.groupCount() == 3 && regex2.groupCount() == 3 && regex3.groupCount() == 3)
									{
										if(regex.find() & regex2.find() && regex3.find())
										{
											data.add(new WeekInfo(regex.group(1), regex.group(2), regex.group(3).charAt(0), regex2.group(1), regex2.group(2), regex2.group(3).charAt(0), regex3.group(1), regex3.group(2), regex3.group(3).charAt(0)));
										}
									}

								}
								int amount = 0;
								for(WeekInfo info : data)
								{
									if(parts.size() == 2 && parts.get(1).equalsIgnoreCase("dpfund"))
									{
										if(info.paid1 == 'n')
										{
											amount += 1000000;
										}
										if(info.paid2 == 'n')
										{
											amount += 500000;
										}
										if(info.paid3 == 'n')
										{
											amount += 250000;
										}
									}
									else
									{
										if(info.paid1 == 'n' && info.name1.equalsIgnoreCase(name))
										{
											amount += 1000000;
										}
										else if(info.paid2 == 'n' && info.name2.equalsIgnoreCase(name))
										{
											amount += 500000;
										}
										else if(info.paid3 == 'n' && info.name3.equalsIgnoreCase(name))
										{
											amount += 250000;
										}
									}
								}
								scanner.close();
								if(parts.size() == 2 && parts.get(1).equalsIgnoreCase("dpfund"))
								{
									printMessage("There is currently: " + NumberFormat.getInstance().format(amount) + " in the drop party fund.",type);
									taskComplete=true;
									return;
								}
								printMessage(name + " may collect: " + NumberFormat.getInstance().format(amount),type);
								taskComplete=true;
								return;
							} catch (Exception e) {
								println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
							}
						}
					}
					else if(parts.get(0).equalsIgnoreCase("discord"))
					{
						if(parts.size() == 2 && (player_rank.equalsIgnoreCase("gold") || player_rank.equalsIgnoreCase("silver") || player_rank.equalsIgnoreCase("bronze")) && parts.get(1).equalsIgnoreCase("toggle"))
						{
							if(recordChat)
							{
								recordChat = false;
								printMessage("Chat feeding: —O—F—F—",type);
							}
							else
							{
								recordChat = true;
								printMessage("Chat feeding: —O—N—",type);
							}
						}
						else
						{
							if(!recordChat)
							{
								try{
									String message = command.substring(10);
									//General.println(message);
									if(message.length() >=1)
									{
										URL url = new URL("https://discordapp.com/api/webhooks/256036445020618752/7AADcPs4VlNtjtCFD6Pw6I2UDujka5r7mnlvpjBI-YVxp1oZ4Q6LbhinO-bOSPcHR8ec");
										URLConnection con = url.openConnection();
										HttpURLConnection http = (HttpURLConnection)con;
										http.setRequestMethod("POST"); // PUT is another valid option
										http.setDoOutput(true);
										byte[] out = ("{\"username\":\"" + name + "\",\"content\":\"" + message + "\",\"avatar_url\":\"http://i.imgur.com/AGhIk86.jpg\"}").getBytes(StandardCharsets.UTF_8);
										int length = out.length;
										http.setFixedLengthStreamingMode(length);
										http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
										http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
										http.connect();
										try(OutputStream os = http.getOutputStream()) {
											os.write(out);
										}
									}
									else
									{
										printMessage("Invalid message length.",type);
									}

								}catch(Exception e)
								{
									println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
								}
							}
						}
						taskComplete=true;
						return;
					}
					else if(parts.get(0).equalsIgnoreCase("randomskill") && name.equalsIgnoreCase("xmax"))
					{
						String[] skills = {"Prayer","Runecrafting","Construction","Agility","Herblore","Thieving","Crafting","Fletching","Slayer","Hunter","Mining","Smithing","Fishing","Cooking","Firemaking","Woodcutting","Farming"};
						int random = General.random(0, skills.length-1);
						printMessage("Skill that was randomly chosen: " + skills[random],type);
						taskComplete=true;
						return;
					}
					else if(parts.get(0).equalsIgnoreCase("endofcomp") && (name.equalsIgnoreCase("xmax") || name.equalsIgnoreCase("Cynes")))
					{
						try
						{

							if(parts.size() >= 2)
							{
								if(parts.get(1).equalsIgnoreCase("update") && parts.size() == 3)
								{
									General.println(parts.get(2));
									int ID = Integer.parseInt(parts.get(2));
									General.println(ID);
									FileWriter fw = new FileWriter(new File(MainPath + "/CurrentID.txt"),false);
									fw.write(String.valueOf(ID));
									fw.close();
									printMessage("Updated comp id to: " + ID,type);
									taskComplete=true;
									return;
								}
								else if(parts.size() == 2)
								{
									int ID = Integer.parseInt(parts.get(1));
									URL url = new URL("http://crystalmathlabs.com/tracker/api.php?type=comprankings&competition="+ID);
									URLConnection con = url.openConnection();
									HttpURLConnection http = (HttpURLConnection) con;
									http.setRequestMethod("GET");
									http.setDoOutput(true);
									http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
									http.connect();
									Reader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
									StringBuilder sb = new StringBuilder();
									for (int c; (c = in.read()) >= 0;)
										sb.append((char)c);
									String response = sb.toString();
									if(response.length() >= 30)
									{
										String[] ar = response.split("[,\\s]");
										File topFile = new File(MainPath+"/Top_Comp_Temp.txt");
										if(!topFile.exists())
										{
											topFile.createNewFile();
										}

										FileWriter fw = new FileWriter(topFile,true);
										for(String s: ar)
										{
											fw.write(s + System.lineSeparator());
										}
										fw.close();
										List<String> genList = new ArrayList<String>();
										Scanner s = new Scanner(topFile);
										int i =0;
										while(s.hasNextLine())
										{
											String line = s.nextLine();
											if(i==0 || i%4==0)
											{
												genList.add(line.replaceAll("_", " "));
												s.nextLine();
												s.nextLine();
												line = s.nextLine();
												genList.add(line);
												i+=3;
											}
											i++;
										}
										s.close();
										topFile.delete();
										File genFile = new File(MainPath+"/genFile.txt");
										if(genFile.exists())
										{
											genFile.delete();
										}
										genFile.createNewFile();
										FileWriter p = new FileWriter(genFile,false);
										p.write("");
										p.close();
										FileWriter k = new FileWriter(genFile,true);
										//General.println(genList.size());
										k.write("x of y - stuff" + System.lineSeparator() + "[gold]1. " + genList.get(0) + ": +" + NumberFormat.getInstance().format(Integer.valueOf(genList.get(1))) + " xp[/gold]" + System.lineSeparator() + "[chocolate]2. " + genList.get(2) + ": +" + NumberFormat.getInstance().format(Integer.valueOf(genList.get(3))) + " xp[/chocolate]" + System.lineSeparator() + "[firebrick]3. " + genList.get(4) + ": +" + NumberFormat.getInstance().format(Integer.valueOf(genList.get(5))) + " xp[/firebrick]" + System.lineSeparator());

										for(int index1 =0;index1 < genList.size();index1+=2)
										{
											//General.println(y);
											k.write(genList.get(index1) + System.lineSeparator());
										}
										int total = 0;
										for(int index2=1 ; index2<genList.size();index2+=2)
										{
											total+=Integer.parseInt(genList.get(index2));
											//General.println(y);
											k.write("'+" + NumberFormat.getInstance().format(Integer.parseInt(genList.get(index2))) + System.lineSeparator());
										}
										k.write("'+" + NumberFormat.getInstance().format(total));
										k.close();
									}
								}

							}
							else if(parts.size() == 1)
							{
								List<String> genList = new ArrayList<String>();
								Scanner s = new Scanner(new File("Top_comp.txt"));
								int i =0;
								while(s.hasNextLine())
								{
									String line = s.nextLine();
									if(i==0 || i%4==0)
									{
										genList.add(line);
										s.nextLine();
										s.nextLine();
										line = s.nextLine();
										genList.add(line);
										i+=3;
									}
									i++;
								}
								s.close();
								File genFile = new File(MainPath+"/genFile.txt");
								if(genFile.exists())
								{
									genFile.delete();
								}
								genFile.createNewFile();
								FileWriter k = new FileWriter(genFile,true);
								k.write("x of y - stuff" + System.lineSeparator() + "[gold]1. " + genList.get(0) + ": +" + NumberFormat.getInstance().format(Integer.valueOf(genList.get(1))) + " xp[/gold]" + System.lineSeparator() + "[chocolate]2. " + genList.get(2) + ": +" + NumberFormat.getInstance().format(Integer.valueOf(genList.get(3))) + " xp[/chocolate]" + System.lineSeparator() + "[firebrick]3. " + genList.get(4) + ": +" + NumberFormat.getInstance().format(Integer.valueOf(genList.get(5))) + " xp[/firebrick]" + System.lineSeparator());

								for(int index1 =0;index1 < genList.size();index1+=2)
								{
									//General.println(y);
									k.write(genList.get(index1) + System.lineSeparator());
								}
								int total = 0;
								for(int index2=1 ; index2<genList.size();index2+=2)
								{
									total+=Integer.parseInt(genList.get(index2));
									//General.println(y);
									k.write("'+" + NumberFormat.getInstance().format(Integer.parseInt(genList.get(index2))) + System.lineSeparator());
								}
								k.write("'+" + NumberFormat.getInstance().format(total));
								k.close();
							}
							printMessage("Generated the file.",type);
							taskComplete=true;
							return;
						}
						catch(Exception e)
						{
							println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

						}
					}
					else if(parts.get(0).equalsIgnoreCase("whotodeletefromcml") && name.equalsIgnoreCase("xmax"))
					{
						try
						{
							String built = "";
							File file = new File(MainPath,"toDeleteFromCML.txt");
							if(!file.exists())
							{
								file.createNewFile();
							}
							for(int i=0; i<list.size();i+=4)
							{
								//General.println(i);
								String player = list.get(i).replaceAll(" ", "_").replaceAll(" ", "_");
								int rankNo = -1;
								if(!list.get(i+1).equals(""))
								{
									rankNo = Integer.parseInt(list.get(i+1));
								}
								if(rankNo == 0)
								{
									built += player + System.lineSeparator();
								}
							}
							
							FileWriter fw = new FileWriter(file,false);
							fw.write(built);
							fw.close();
							printMessage("Marked all the required nubs",type);
							taskComplete=true;
							return;
						}
						catch(Exception e)
						{
							println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
						}
					}
					else{
						Logging("Invalid command detected",type);
						printMessage("Invalid command",type);
						taskComplete=true;
						return;
					}
				}
				else
				{
					Logging("No command detected",type);
					printMessage("Must enter a command",type);
				}
			}
			else{
				Logging("Unqualified rank detected",type);
				println("Unqualified rank");
				if(System.currentTimeMillis()-lastBadRank >= 300000)
				{
					lastBadRank = System.currentTimeMillis();
					printMessage("You must be ranked to use the bot.",true);
				}
			}
		}
		else{
			Logging("Failed to get player component",type);
			println("Failed to get component");
			printMessage("Failed getting player interface component. Report this to M'Lord A.S.A.P",type);
		}
		taskComplete=true;
	}
	/*private boolean eventCreator(String name2, List<String> parts2) {
		Logging("Event creator",true);
		Integer interval;
		try{
			interval=new Integer(parts2.get(3));
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		catch(NullPointerException e)
		{
			return false;
		}
		if(parts2.get(2)!= null && parts.get(2)!= "")
		{
			ArrayList<String> detailsList=new ArrayList<String>();
			String details="";
			ArrayList<String> fullDetails=new ArrayList<String>();
			fullDetails.add(name2);
			fullDetails.add(interval.toString());
			String starttime=Long.toString(System.currentTimeMillis());

			if(parts.size() >= 5)
			{
				for(int i=4;i<parts.size();i++)
				{
					detailsList.add(parts.get(i));
				}
				for(String s:detailsList)
				{
					details += s+" ";
				}
				detailsList.clear();

				fullDetails.add(details);
				fullDetails.add(starttime);
				fullDetails.add("false");
				//println(fullDetails);
				if(!eventMap.containsKey(parts2.get(2).toLowerCase()))
				{
					eventMap.putIfAbsent(parts.get(2).toLowerCase(), fullDetails);
					//println(eventMap.values());
					return true;
				}
				else{
					return false;
				}
			}
			else{
				fullDetails.add("");
				fullDetails.add(starttime);
				fullDetails.add("false");
				if(!eventMap.containsKey(parts2.get(2).toLowerCase()))
				{
					eventMap.putIfAbsent(parts.get(2).toLowerCase(), fullDetails);
					return true;
				}
				else{
					return false;
				}
			}
		}
		else{
			return false;
		}
	}*/
	public boolean updateExamine(String name, String examine)
	{
		try{
			File examineFile=new File(MainPath+"/ItemDB/",name+".txt");
			if(!examineFile.exists())
			{
				if(examine.toLowerCase().equals("delete"))
				{
					return true;
				}
				examineFile.createNewFile();
				FileWriter fw=new FileWriter(examineFile,false);
				fw.write("\"description\":\""+examine+"\"");
				fw.close();
				return true;
			}
			else
			{
				if(examine.toLowerCase().equals("delete"))
				{
					examineFile.delete();
					return true;
				}
				FileReader reader=new FileReader(examineFile);
				BufferedReader breader=new BufferedReader(reader);
				String temp=null;
				String temp2=null;
				while((temp=breader.readLine()) != null)
				{
					temp2+=temp;
				}
				breader.close();
				//println(temp2);

				if(temp2.contains("{\"icon\""))
				{
					return false;
				}
				else
				{
					FileWriter fw=new FileWriter(examineFile,false);
					fw.write("\"description\":\""+examine+"\"");
					fw.close();
					return true;
				}
			}
		}
		catch(Exception e)
		{
			println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }
		}
		return false;
	}
	public String verifyName(String name,String oldName)
	{
		int loginUpdate=0;
		int charUpdate=0;
		int timeUpdate=0;
		try{
			int retries=0;
			GameTab.open(TABS.FRIENDS);
			RSInterface nameBubble=Interfaces.get(429, 10);
			RSInterface flist=Interfaces.get(429,8);
			while(flist.getChild(0)!=null)
			{
				for(RSInterface tempComponent:flist.getChildren())
				{
					if(tempComponent.getActions()!=null)
					{
						for(String tempAction:tempComponent.getActions())
						{
							if(tempAction.equalsIgnoreCase("Delete"))
							{
								Clicking.click("Delete",tempComponent);
								General.sleep(1000);	
							}
						}
					}
				}
				if(retries>=6)
				{
					return null;
				}
				retries++;
			}
			retries=0;
			while(true)
			{
				if(retries>=5)
				{
					return "Failed adding player";
				}
				retries++;
				Clicking.click(Interfaces.get(429, 12));
				Timing.waitCondition(new Condition(){

					@Override
					public boolean active() {
						General.sleep(100);
						return Interfaces.get(162, 32).getText().toLowerCase().equals("Enter name of friend to add to list".toLowerCase());
					}
				}, General.random(3000, 3500));
				if(Interfaces.get(162, 32).getText().toLowerCase().equals("Enter name of friend to add to list".toLowerCase()))
				{
					Keyboard.typeSend(name);
					Timing.waitCondition(new Condition(){

						@Override
						public boolean active() {
							General.sleep(100);
							RSInterface flist=Interfaces.get(429, 8);
							return flist.getChild(0)!=null;
						}

					}, General.random(2000, 2500));
					retries=0;
					break;
				}
			}
			//println("2");
			while(true){
				if(retries>=5)
				{
					return "Failed confirming identity.";
				}
				retries++;
				for(RSInterface temp2:flist.getChildren())
				{
					//println("3");
					if(temp2.getActions()!=null)
					{
						//println("4");
						if(temp2.getText().replaceAll(" ", " ").equalsIgnoreCase(name))
						{
							//println("5");
							temp2.hover();
							pauseLoginData=true;
							General.sleep(2000);
							Matcher match=Pattern.compile("<br>(.*)").matcher(nameBubble.getChild(2).getText().replaceAll(" ", " "));
							if(match.find())
							{
								if(match.group(1).equalsIgnoreCase(oldName))
								{
									File currentFileLoginData=new File(MainPath+"/playerLogin/",name+".txt");
									File currentFileCharData=new File(MainPath+"/players/",name+".txt");
									File currentFileTimeData=new File(MainPath+"/playersData/",name+".txt");
									File oldFileLoginData=new File(MainPath+"/playerLogin/",oldName+".txt");
									File oldFileCharData=new File(MainPath+"/players/",oldName+".txt");
									File oldFileTimeData=new File(MainPath+"/playersData/",oldName+".txt");
									//Login data updater
									try{
										if(!oldFileLoginData.exists())
										{
											loginUpdate=2;
										}
										else{
											Files.copy(currentFileLoginData.toPath(), (new File(MainPath+"/backupData/backupLoginCurrent_"+currentFileLoginData.getName())).toPath(),StandardCopyOption.REPLACE_EXISTING);
											Files.copy(oldFileLoginData.toPath(), (new File(MainPath+"/backupData/backupLoginOld_"+oldFileLoginData.getName())).toPath(),StandardCopyOption.REPLACE_EXISTING);
											Scanner scanOld=new Scanner(oldFileLoginData);
											Scanner scanCurrent=new Scanner(currentFileLoginData);
											ArrayList<String> currentData=new ArrayList<String>();
											ArrayList<String> oldData=new ArrayList<String>();
											while(scanOld.hasNextLine())
											{
												oldData.add(scanOld.nextLine());
											}
											scanOld.close();
											while(scanCurrent.hasNextLine())
											{
												currentData.add(scanCurrent.nextLine());
											}
											scanCurrent.close();
											FileWriter clearFile=new FileWriter(currentFileLoginData,false);
											clearFile.write("");
											clearFile.close();
											FileWriter writeNewData=new FileWriter(currentFileLoginData,true);
											for(String line:oldData)
											{
												writeNewData.write(line+System.lineSeparator());
											}
											for(String line:currentData)
											{
												writeNewData.write(line+System.lineSeparator());
											}
											writeNewData.close();
											//sleep(500);
											Files.delete(oldFileLoginData.toPath());
											loginUpdate=1;
										}
									}catch(Exception e)
									{
										println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }
									}
									//Char data updater
									try{
										if(!oldFileCharData.exists())
										{
											charUpdate=2;
										}
										else{
											Files.copy(currentFileCharData.toPath(), (new File(MainPath+"/backupData/backupCharCurrent_"+currentFileCharData.getName())).toPath(),StandardCopyOption.REPLACE_EXISTING);
											Files.copy(oldFileCharData.toPath(), (new File(MainPath+"/backupData/backupCharOld_"+oldFileCharData.getName())).toPath(),StandardCopyOption.REPLACE_EXISTING);
											Scanner scanOld=new Scanner(oldFileCharData);
											Scanner scanCurrent=new Scanner(currentFileCharData);
											ArrayList<String> currentData=new ArrayList<String>();
											ArrayList<String> oldData=new ArrayList<String>();
											while(scanOld.hasNextLine())
											{
												oldData.add(scanOld.nextLine());
											}
											scanOld.close();
											while(scanCurrent.hasNextLine())
											{
												currentData.add(scanCurrent.nextLine());
											}
											scanCurrent.close();
											long oldChars=Long.parseLong(oldData.get(0).substring(23));
											long newChars=Long.parseLong(currentData.get(0).substring(23));
											long oldWords=Long.parseLong(oldData.get(1).substring(18));
											long newWords=Long.parseLong(currentData.get(1).substring(18));
											long currentChars=oldChars+newChars;
											long currentWords=oldWords+newWords;
											FileWriter clearFile=new FileWriter(currentFileCharData,false);
											clearFile.write("");
											clearFile.close();
											FileWriter writeNewData=new FileWriter(currentFileCharData,true);
											writeNewData.write("total characters typed:"+currentChars+System.lineSeparator()+"total words typed:"+currentWords);
											writeNewData.close();
											//sleep(500);
											Files.delete(oldFileCharData.toPath());
											charUpdate=1;
										}
									}catch(Exception e)
									{
										println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }
									}
									//Time data updater
									try{
										if(!oldFileTimeData.exists())
										{
											timeUpdate=2;
										}
										else{
											Files.copy(currentFileTimeData.toPath(), (new File(MainPath+"/backupData/backupTimeCurrent_"+currentFileTimeData.getName())).toPath(),StandardCopyOption.REPLACE_EXISTING);
											Files.copy(oldFileTimeData.toPath(), (new File(MainPath+"/backupData/backupTimeOld_"+oldFileTimeData.getName())).toPath(),StandardCopyOption.REPLACE_EXISTING);
											Scanner scanOld=new Scanner(oldFileTimeData);
											Scanner scanCurrent=new Scanner(currentFileTimeData);
											ArrayList<String> currentData=new ArrayList<String>();
											ArrayList<String> oldData=new ArrayList<String>();
											while(scanOld.hasNextLine())
											{
												oldData.add(scanOld.nextLine());
											}
											scanOld.close();
											while(scanCurrent.hasNextLine())
											{
												currentData.add(scanCurrent.nextLine());
											}
											scanCurrent.close();
											long oldTime=Long.parseLong(oldData.get(1).substring(11));
											long newTime=Long.parseLong(currentData.get(1).substring(11));
											long currentTime=oldTime+newTime;
											long lastLogin=Long.parseLong(currentData.get(0).substring(9));
											FileWriter clearFile=new FileWriter(currentFileTimeData,false);
											clearFile.write("");
											clearFile.close();
											FileWriter writeNewData=new FileWriter(currentFileTimeData,true);
											writeNewData.write("lastseen:"+lastLogin+System.lineSeparator()+"time spent:"+currentTime);
											writeNewData.close();
											//sleep(500);
											Files.delete(oldFileTimeData.toPath());
											timeUpdate=1;
										}
									}catch(Exception e)
									{
										println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }
									}
									return String.valueOf(loginUpdate)+String.valueOf(charUpdate)+String.valueOf(timeUpdate);
								}
							}
						}

					}
				}
				General.sleep(1000);
			}
		}
		catch(Exception e)
		{
			println(e.toString()); Logging(e.toString(),true); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),true); }
		}
		return null;
	}
	public void printMessage(String msg,boolean type){
		try{
			try
			{
				if(type)
				{
					if(commandIssuer.get(0).replaceAll(" ", "_").replaceAll(" ", "_").equalsIgnoreCase("xmax"))
					{
						msg="M'lord, "+msg;
					}
					if(commandIssuer.get(0).replaceAll(" ", "_").replaceAll(" ", "_").equalsIgnoreCase("cynes"))
					{
						msg="M'lady, "+msg;
					}
				}
			}
			catch(Exception e)
			{

				println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }
			}
			Logging("PrintMessage method",type);
			int retries=0;
			Double speed=0.00001;
			//println("print message");
			RSInterfaceChild flist=Interfaces.get(429, 8);
			Keyboard.setSpeed(speed);
			int counter=0;
			int temp=msg.length();
			//println(temp);
			Logging("temp message size: "+temp,type);
			while(temp>=75)
			{
				if(!type)
				{
					Logging("True for [PM] and [Length>=75]",type);
					GameTab.open(TABS.FRIENDS);
					if(flist.getChild(0)!=null)
					{
						Logging("Friends list no empty, time to delete",type);
						//Logging("Friends list contains: "+flist.getChildren(),type);
						if(flist.getChildren().length>0)
						{
							//Logging("Friends list contains: "+flist.getChildren().length+" friends",type);
							//println("Deleting friends");
							//int i=0;
							while(flist.getChild(0)!=null)
							{
								for(RSInterfaceComponent tempComponent:flist.getChildren())
								{
									//println(flist.getChildren().length);
									//println(i);
									if(tempComponent.getActions()!=null)
									{
										for(String tempAction:tempComponent.getActions())
										{
											if(tempAction.equalsIgnoreCase("Delete"))
											{
												//println(i);
												//println("test1");
												Clicking.click("Delete",tempComponent);
												//println("test2");
												Logging("Deleting Friend",type);
												//println("test3");
												//i=-1;
												General.sleep(1000);	
												//println("test4");
											}
										}
									}
									//General.sleep(1000,1500);
									//flist=Interfaces.get(429, 3);
									/*if(retries2 >=3)
								{
									break;
								}
								retries2++;*/
									//i++;
									//println("I got here");
								}
								if(retries>=6)
								{
									break;
								}
								retries++;

							}
						}
					}
					while(true)
					{
						Logging("Friend messaging attempp: "+retries,type);
						if(retries>=6)
						{
							break;
						}
						if(flist.getChild(0) != null)
						{
							retries++;
							Logging("Person exists, let us pm them.",type);
							Clicking.click("Message",flist.getChild(0));
							Timing.waitCondition(new Condition(){

								@Override
								public boolean active() {
									General.sleep(100);
									return Interfaces.get(162, 32).getText().toLowerCase().contains("enter message to send to".toLowerCase());
								}
							}, General.random(1000, 1500));
							if(Interfaces.get(162, 32).getText().toLowerCase().contains("enter message to send to".toLowerCase()))
							{
								Keyboard.typeSend(msg.substring(counter*75, (counter+1)*75));
								Logging("Typed message",type);
								counter++;
								temp=temp-(counter*75);
								if(temp>=75)
								{
									Logging("Message length still greater than 75",type);
									continue;
								}
								else{
									break;
								}
							}
						}
						else{
							retries++;
							Logging("Adding friend",type);
							Clicking.click(Interfaces.get(429, 12));
							Timing.waitCondition(new Condition(){

								@Override
								public boolean active() {
									General.sleep(100);
									return Interfaces.get(162, 32).getText().toLowerCase().equals("Enter name of friend to add to list".toLowerCase());
								}
							}, General.random(3000, 3500));
							if(Interfaces.get(162, 32).getText().toLowerCase().equals("Enter name of friend to add to list".toLowerCase()))
							{
								Logging("Adding: "+commandIssuerPM.get(0)+" to friends list",type);

								//println(commandIssuerPM.get(0) + " "+ commandPM.get(0));
								Keyboard.typeSend(commandIssuerPM.get(0).replaceAll(" ", "_"));
								Timing.waitCondition(new Condition(){

									@Override
									public boolean active() {
										General.sleep(100);
										RSInterface flist=Interfaces.get(429, 8);
										return flist.getChild(0)!=null;
									}

								}, General.random(2000, 2500));
							}
						}
						General.sleep(1000);
						//retries++;
					}
				}
				else{
					Logging("Message is longer than 75 chars and is in CC chat. sending starting characters characters.",type);
					Keyboard.typeSend("/"+msg.substring(counter*75, (counter+1)*75));
					counter++;
					temp=temp-(counter*75);
					//println(temp);
				}
			}
			if(!type)
			{
				Logging("message is in PM and is NOT longer than 75 characters.",type);
				GameTab.open(TABS.FRIENDS);
				if(flist.getChild(0)!=null)
				{		
					//Logging("Friends list contains: "+flist.getChildren(),type);
					if(flist.getChildren().length>0)
					{
						/*ArrayList<Integer> indexNumber=new ArrayList<Integer>();

						for(int t=0;t<flist.getChildren().length;t++)
						{
							if(flist.getChild(t).getActions()!=null)
							{
								for(String tempAction:flist.getChild(t).getActions())
								{
									if(tempAction.equalsIgnoreCase("Delete"))
									{
										indexNumber.add(t);
									}
								}
							}
						}
						println(indexNumber);*/
						//println(flist.getChildren().length);
						while(flist.getChild(0)!=null)
						{
							for(RSInterfaceComponent tempComponent:flist.getChildren())
							{
								//println(flist.getChildren().length);
								//println(i);
								if(tempComponent.getActions()!=null)
								{
									for(String tempAction:tempComponent.getActions())
									{
										if(tempAction.equalsIgnoreCase("Delete"))
										{
											//println(i);
											//println("test1");
											Clicking.click("Delete",tempComponent);
											//println("test2");
											Logging("Deleting Friend",type);
											//println("test3");
											//i=-1;
											General.sleep(1000);	
											//println("test4");
										}
									}
								}
								//General.sleep(1000,1500);
								//flist=Interfaces.get(429, 3);
								/*if(retries2 >=3)
							{
								break;
							}
							retries2++;*/
								//i++;
								//println("I got here");
							}
							if(retries>=6)
							{
								break;
							}
							retries++;

						}
					}
				}
				//println("test5");
				while(true)
				{
					Logging("Attempt number: "+retries,type);
					if(retries>=6)
					{
						break;
					}
					//println("test6");
					if(flist.getChild(0) != null)
					{
						//println("test8");
						retries++;
						Clicking.click("Message",flist.getChild(0));
						Timing.waitCondition(new Condition(){

							@Override
							public boolean active() {
								General.sleep(100);
								return Interfaces.get(162, 32).getText().toLowerCase().contains("enter message to send to".toLowerCase());
							}

						}, General.random(1000, 1500));
						if(Interfaces.get(162, 32).getText().toLowerCase().contains("enter message to send to".toLowerCase()))
						{
							Logging("Sending message to person in PM ",type);
							Keyboard.typeSend(msg.substring(counter*75, msg.length()));
							break;
						}
					}
					else{
						//println("test7");
						retries++;
						Clicking.click(Interfaces.get(429, 12));
						Timing.waitCondition(new Condition(){

							@Override
							public boolean active() {
								General.sleep(100);
								return Interfaces.get(162, 32).getText().toLowerCase().equals("Enter name of friend to add to list".toLowerCase());
							}
						}, General.random(3000, 3500));
						if(Interfaces.get(162, 32).getText().toLowerCase().equals("Enter name of friend to add to list".toLowerCase()))
						{
							Logging("Adding: "+commandIssuerPM.get(0)+" To friends list",type);
							//println(commandIssuerPM.get(0)+" 1");
							//println(commandIssuerPM.get(0).replaceAll(" ", "_")+" 2");
							Keyboard.typeSend(commandIssuerPM.get(0).replaceAll(" ", "_"));
							Timing.waitCondition(new Condition(){

								@Override
								public boolean active() {
									General.sleep(100);
									RSInterface flist=Interfaces.get(429, 8);
									return flist.getChild(0)!=null;
								}

							}, General.random(2000, 2500));
						}
						General.sleep(1000);
						//retries++;
					}
				}
				GameTab.open(TABS.CLAN);
				return;
			}
			//println(msg);
			Logging("Message length less than 75 and is in CC chat and sending rest of characters",type);
			Keyboard.typeSend("/"+msg.substring(counter*75,msg.length()));
			if(msg.toLowerCase().contains("a q p was not found"))
			{
				Keyboard.typeSend("/     W");
			}
		}
		catch(Exception e)
		{
			println(e.toString()); Logging(e.toString(),type); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),type); }

			return;
		}
	}
	@Override
	public void duelRequestReceived(String arg0, String arg1) {
	}
	@Override
	public void personalMessageReceived(String name, String msg) {
		name=name.replace("<img=2>", "").replace("<img=0>", "").replace("<img=1>", "").replace("<img=3>", "").replaceAll(" ", " ").replaceAll(" ", " ");
		if(pollRunning)
		{
			//println(voters);
			//println(votes);
			//println(msg);
			//println(name);
			//println("Message length = "+msg.length());
			if(!voters.contains(name) && msg.length()==1 && (msg.toLowerCase().startsWith("y") || msg.toLowerCase().startsWith("n")))
			{
				voters.add(name);
				votes.add(msg.toLowerCase());
			}
		}
		Thread commandManagerPM=new Thread(new Runnable(){

			@Override
			public void run() {
				while(commandIssuerPM.size() >0)
				{
					Logging("Waiting for command to be completed [PM] ",false);
					int timer=0;
					while(!taskComplete && timer <=150)
					{
						println("Waiting for command to complete");
						sleep(100);
						timer++;
					}
					if(timer>=149)
					{
						Logging("Command timed out, resetting",false);
						commandIssuerPM.remove(0);
						commandPM.remove(0);
					}
					//println("About to execute command");
					if(commandIssuerPM.size()>0)
					{
						Logging("Determing command [PM]",false);
						Logging("issuer: "+commandIssuerPM+" Command: "+commandPM,false);
						determineCommand(commandIssuerPM.get(0),commandPM.get(0),false);
						commandIssuerPM.remove(0);
						commandPM.remove(0);
						Logging("Command complete [PM] ",false);
					}
				}
			}
		});
		SimpleDateFormat date=new SimpleDateFormat("MMM d, YYYY");
		date.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat time=new SimpleDateFormat("HH:mm:ss");
		time.setTimeZone(TimeZone.getTimeZone("UTC"));
		file=new File(MainPath+"/OSS_LOGS/",date.format(System.currentTimeMillis())+".txt");
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
				println("New date started: "+file.getName());
			} catch (IOException e) {

				println(e.toString()); Logging(e.toString(),false); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),false); }
			}
		}
		try {
			FileWriter fw=new FileWriter(file,true);
			fw.write("[PM]["+time.format(System.currentTimeMillis())+"] "+name+": "+msg+System.lineSeparator());
			fw.close();
			//fw.flush();
		} catch (IOException e) {
			println(e.toString()); Logging(e.toString(),false); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]); Logging(e.getStackTrace()[f].toString(),false); }

		}
		if(msg.startsWith("!!"))
		{
			commandIssuerPM.add(name);
			commandPM.add(msg);
			Logging(commandIssuerPM+" : "+commandPM,false);
			if(!commandManagerPM.isAlive())
			{
				commandManagerPM.start();
			}
		}
	}
	@Override
	public void playerMessageReceived(String arg0, String arg1) {
	}
	@Override
	public void serverMessageReceived(String arg0) {
	}
	@Override
	public void tradeRequestReceived(String arg0) {
	}
	/*private void scroll(Rectangle plist, RSInterfaceComponent playerComp) {
		while(!plist.contains(Mouse.getPos()))
		{
			Mouse.moveBox(plist);
		}
		while(!plist.contains(playerComp.getAbsoluteBounds()))
		{
			println("Scrolling");
			Mouse.scroll(playerComp.getAbsoluteBounds().y < plist.y);
			General.sleep(60,80);
		}
	}*/
	public boolean CCTabOpen(){
		GameTab.open(TABS.CLAN);
		return true;
	}
	public RSInterfaceComponent getPlayer(String player)
	{

		GameTab.open(TABS.CLAN);
		RSInterfaceChild CCList=Interfaces.get(7, 14);
		if(CCList != null)
		{
			for(RSInterfaceComponent z:CCList.getChildren())
			{
				if(z.getText().toLowerCase().replace("<img=2>", "").replace("<img=0>", "").replace("<img=1>", "").replace("<img=3>", "").replaceAll(" ", " ").replaceAll(" ", " ").equals(player.toLowerCase()))
				{
					return z;
				}
			}
		}
		return null;
	}
	public void Logging(String action,boolean type)
	{
		String chat;
		if(type)
		{
			chat="[CC]";
		}
		else{
			chat="[PM]";
		}
		try {
			FileWriter logger=new FileWriter(logging,true);
			SimpleDateFormat time=new SimpleDateFormat("HH:mm:ss");
			time.setTimeZone(TimeZone.getTimeZone("UTC"));
			logger.write(chat+"["+time.format(System.currentTimeMillis())+"] "+action+System.lineSeparator());
			logger.close();
			//fw.flush();
		} catch (IOException e) {
			println(e.toString()); for(int f=0;f<e.getStackTrace().length;f++) { println(e.getStackTrace()[f]);}
		}
	}

	public double eval(final String str) {
		try{
			return new Object() {
				int pos = -1, ch;

				void nextChar() {
					ch = (++pos < str.length()) ? str.charAt(pos) : -1;
				}

				boolean eat(int charToEat) {
					while (ch == ' ') nextChar();
					if (ch == charToEat) {
						nextChar();
						return true;
					}
					return false;
				}

				double parse() {
					nextChar();
					double x = parseExpression();
					return x;
				}

				// Grammar:
				// expression = term | expression `+` term | expression `-` term
				// term = factor | term `*` factor | term `/` factor
				// factor = `+` factor | `-` factor | `(` expression `)`
				//        | number | functionName factor | factor `^` factor

				double parseExpression() {
					double x = parseTerm();
					for (;;) {
						if      (eat('+')) x += parseTerm(); // addition
						else if (eat('-')) x -= parseTerm(); // subtraction
						else return x;
					}
				}

				double parseTerm() {
					double x = parseFactor();
					for (;;) {
						if      (eat('*')) x *= parseFactor(); // multiplication
						else if (eat('/')) x /= parseFactor(); // division
						else return x;
					}
				}

				double parseFactor() {
					if (eat('+')) return parseFactor(); // unary plus
					if (eat('-')) return -parseFactor(); // unary minus

					double x;
					int startPos = this.pos;
					if (eat('(')) { // parentheses
						x = parseExpression();
						eat(')');
					} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
						while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
						x = Double.parseDouble(str.substring(startPos, this.pos));
					} else if (ch >= 'a' && ch <= 'z') { // functions
						while (ch >= 'a' && ch <= 'z') nextChar();
						String func = str.substring(startPos, this.pos);
						x = parseFactor();
						if (func.equals("sqrt")) x = Math.sqrt(x);
						else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
						else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
						else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					} else {
						println("Exception at calculation");
						x=0;
					}

					if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

					return x;
				}
			}.parse();
		}
		catch(Exception t)
		{
			println(t.toString()); Logging(t.toString(),true); for(int f=0;f<t.getStackTrace().length;f++) { println(t.getStackTrace()[f]); Logging(t.getStackTrace()[f].toString(),true); }
		}
		return 0;
	}
	public boolean getCMLData(String username, long time, String skill, boolean type)
	{
		skill = skill.toLowerCase();
		username = username.replaceAll(" ", "_").replaceAll(" ", "_");
		LinkedHashMap<String,String> levelData = new LinkedHashMap<String,String>();
		levelData.clear();
		String[] skillsAbbreviated = {"ttl","att","def","str","hp","rng","pray","magic","cook","wc","fletch","fish","fm","craft","smith","mng","herb","agil","thiev","slay","farm","rc","hunt","con"};
		String[] skills = {"total","attack","defence","strength","hitpoints","ranged","prayer","magic","cooking","woodcutting","fletching","fishing","firemaking","crafting","smithing","mining","herblore","agility","thieving","slayer","farming","runecrafting","hunter","construction"};
		for(int i = 0;i<skills.length;i++)
		{
			if(skills[i].equalsIgnoreCase(skill) || (skills[i].startsWith(skill) && skill.length() >= 3) || skillsAbbreviated[i].equalsIgnoreCase(skill) || (skillsAbbreviated[i].startsWith(skill) && skill.length() >=2))
			{
				skill = skills[i];
				try
				{
					URL cmlURL = new URL("http://crystalmathlabs.com/tracker/api.php?type=track&player=" + username + "&time=" + time);
					URLConnection cmlConnection = cmlURL.openConnection();
					cmlConnection.setRequestProperty("Accept-Charset", java.nio.charset.StandardCharsets.UTF_8.name());
					cmlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
					cmlConnection.connect();

					BufferedReader dataReader = new BufferedReader(new InputStreamReader(cmlConnection.getInputStream()));
					String line = null;
					int lineCounter = -1;
					while((line = dataReader.readLine()) != null)
					{
						if(lineCounter == -1)
						{
							lineCounter++;
							continue;
						}
						levelData.put(skills[lineCounter], line);
						lineCounter++;
					}

					String[] requiredData = levelData.get(skill).split(",");

					Long xpChange = Long.valueOf(requiredData[0]);
					Long rankChange = Long.valueOf(requiredData[1])*(-1);
					Long XP = Long.valueOf(requiredData[2]);
					Long rank = Long.valueOf(requiredData[3]);

					printMessage("[" + skill + "] xp change: +" + NumberFormat.getInstance().format(xpChange) + " rank change: " + NumberFormat.getInstance().format(rankChange) + " xp: " + NumberFormat.getInstance().format(XP) + " rank: " + NumberFormat.getInstance().format(rank),type);
					return true;
				}
				catch(Exception t)
				{
					println(t.toString()); Logging(t.toString(),true); for(int f=0;f<t.getStackTrace().length;f++) { println(t.getStackTrace()[f]); Logging(t.getStackTrace()[f].toString(),true); }
				}
			}
		}

		return false;
	}
	public long convertToSeconds(String time)
	{
		time = time.toLowerCase();
		long days = 0;
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		Matcher timeFormat = Pattern.compile("(\\d+[a-zA-Z])?(\\d+[a-zA-Z])?(\\d+[a-zA-Z])?(\\d+[a-zA-Z])?").matcher(time);
		if(timeFormat.find())
		{
			for(int i = 1;i<=timeFormat.groupCount();i++)
			{
				if(timeFormat.group(i) == null)
				{
					continue;
				}
				if(timeFormat.group(i).endsWith("d"))
				{
					days = Long.valueOf(timeFormat.group(i).replaceAll("d", ""));
				}
				else if(timeFormat.group(i).endsWith("h"))
				{
					hours = Long.valueOf(timeFormat.group(i).replaceAll("h", ""));
				}
				else if(timeFormat.group(i).endsWith("m"))
				{
					minutes = Long.valueOf(timeFormat.group(i).replaceAll("m", ""));
				}
				else if(timeFormat.group(i).endsWith("s"))
				{
					seconds = Long.valueOf(timeFormat.group(i).replaceAll("s", ""));
				}
			}
		}
		return ((days*86400)+(hours*3600)+(minutes*60)+seconds);
	}
}

class WeekInfo
{
	String name1;
	String xp1;
	char paid1;
	String name2;
	String xp2;
	char paid2;
	String name3;
	String xp3;
	char paid3;
	public WeekInfo(String name1, String xp1, char paid1, String name2, String xp2, char paid2, String name3, String xp3, char paid3)
	{
		this.name1 = name1;
		this.xp1 = xp1;
		this.paid1 = paid1;
		this.name2 = name2;
		this.xp2 = xp2;
		this.paid2 = paid2;
		this.name3 = name3;
		this.xp3 = xp3;
		this.paid3 = paid3;
	}
}