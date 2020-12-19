package Jemand.Listener;

import Jemand.NumberToText;
import Jemand.Zitat;
import Jemand.func;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.activity.Activity;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.auditlog.AuditLogActionType;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.WebhookMessageBuilder;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.channel.server.ServerChannelDeleteEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.server.member.ServerMemberBanEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.event.server.role.RoleChangePermissionsEvent;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.event.server.role.UserRoleEvent;
import org.javacord.api.event.server.role.UserRoleRemoveEvent;
import org.javacord.api.event.user.*;
import org.javacord.api.util.DiscordRegexPattern;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.core.entity.user.UserImpl;

import java.awt.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GuildUtilities {
    static public final long AN = 367648314184826880L;
    static private final long COPY = 740214894036779009L;
    static public final long MITGLIED = 367649615484551179L;
    static private final long VORLÄUFIG = 559141475812769793L;
    static private final long ROLLENMEISTER = 493572898577973249L;
    static private final long LOGS = 559451873015234560L;
    static private final long ACTIVITY_LOGS = 740338145689600032L;

    static private final long PETITIONEN_KANAL = 681650819002662946L;

    static private final long WITZIG_KANAL = 740498116171792395L;
    static private final long WITZIG_EMOJI = 609012744578007071L;

    static private final long BOT_MITGLIED = 559440621815857174L;
    static private final long LAWLIET_ID = 368521195940741122L;

    private final ConcurrentHashMap<Long, Long> newUsersLastMessage = new ConcurrentHashMap<>();

    private final DiscordApi api;

    public GuildUtilities(DiscordApi api) {
        this.api = api;
        api.getServerById(AN).ifPresent(server -> {
            server.addUserRoleAddListener(this::userAddedRole);
            server.addUserRoleRemoveListener(this::userRemovedRole);
            server.addMessageCreateListener(this::messageCreated);
            server.addServerMemberBanListener(this::memberBanned);
            server.addServerChannelDeleteListener(this::channelDeleted);
            server.addRoleChangePermissionsListener(this::roleChangedPermission);
            server.addUserChangeActivityListener(this::activityChanged);

            server.addUserChangeNameListener(this::userChangedName);
            server.addUserChangeDiscriminatorListener(this::userChangedDiscriminator);
            server.addUserChangeNicknameListener(this::userChangedNickname);
            server.addUserChangeAvatarListener(this::userChangedAvatar);

            server.addServerMemberJoinListener(this::userJoined);
            server.addReactionAddListener(this::reactionAdded);

            server.addUserRoleAddListener(event -> updatedRolesUser(event, true));
            server.addUserRoleRemoveListener(event -> updatedRolesUser(event, false));
        });
    }

    private void userRemovedRole(UserRoleRemoveEvent event) {
        if (event.getRole().getId() == 755058629299667107L //Vote-Rolle top.gg
                && event.getServer().getMembers().contains(event.getUser())) {

            event.getUser().sendMessage("Du kannst hier erneut voten: https://top.gg/servers/367648314184826880/vote");
        }
    }

    private void updatedRolesUser(UserRoleEvent event, boolean added) {
        if (added && event.getRole().getId() == BOT_MITGLIED && !event.getUser().isBot()) {
            event.getUser().removeRole(event.getRole()).exceptionally(ExceptionLogger.get());
        } else {
            EmbedBuilder embed = getUserUpdatedEmbedBuilder(event.getUser());
            embed.addField(added ? "Rolle hinzugefügt:" : "Rolle entfernt:", event.getRole().getName() + " (" + event.getRole().getId() + ")");
            StringBuilder roles = new StringBuilder();
            event.getUser().getRoles(event.getServer()).forEach(role -> roles.append(role.getMentionTag()).append("\n"));
            embed.addField("Rollen:", roles.toString());
            sendEmbedToLogs(embed, event.getApi());
        }
    }

    private void reactionAdded(ReactionAddEvent event) {
        if (event.getEmoji().asCustomEmoji().map(DiscordEntity::getId).orElse(0L) == WITZIG_EMOJI
                && event.getChannel().getId() != WITZIG_KANAL) {
            Message m = event.requestMessage().join();
            m.getReactions().stream().filter(reaction -> reaction.getEmoji().asCustomEmoji().map(DiscordEntity::getId).orElse(0L) == WITZIG_EMOJI).findFirst().ifPresent(reaction -> {
                if (reaction.getCount() == 1) {
                    sendWebhookMessageBuilderToId(m.toWebhookMessageBuilder().setAllowedMentions(new AllowedMentionsBuilder().build()), WITZIG_KANAL, event.getApi()).ifPresent(messageCompletableFuture -> {
                        messageCompletableFuture.thenAccept(message -> {
                            message.addReaction(reaction.getEmoji()).exceptionally(ExceptionLogger.get());

                            message.crossPost().exceptionally(ExceptionLogger.get());

                        });
                    });
                }
            });
        }
        //else if (event.getChannel().getId() == WITZIG_KANAL && event.getEmoji().getNicknameMentionTag().equals(QUESTION_MARK)) {
        //    event.requestMessage().thenAccept(message -> {
        //       if (message.getAuthor().isWebhook() && message.getContent().contains(Channelportal.SEPARATOR)) {
        //            long id = func.binaryBlankStringToLong(message.getContent().split(Channelportal.SEPARATOR, 2)[0]);
        //            event.getUser().sendMessage(func.getApi().getMessageById)
        //       }
        //    });
        //}
    }

    private void userJoined(ServerMemberJoinEvent event) {
        sendEmbedToLogs(getUserUpdatedEmbedBuilder(event.getUser())
                .addField("Erstellt am:", event.getUser().getCreationTimestamp().toString())
                .addField("Name:", event.getUser().getDiscriminatedName())
                , event.getApi());
    }

    private static Optional<CompletableFuture<Message>> sendWebhookMessageBuilderToId(WebhookMessageBuilder wmb, long id, DiscordApi api) {
        return api.getServerTextChannelById(id).flatMap(serverTextChannel -> func.getIncomingWebhook(serverTextChannel).map(webhook -> wmb.send(webhook).exceptionally(ExceptionLogger.get())));
    }

    private static void sendEmbedToLogs(EmbedBuilder embed, DiscordApi api) {
        sendEmbedToId(embed, LOGS, api);
    }

    private static void sendEmbedToId(EmbedBuilder embed, long id, DiscordApi api) {
        sendWebhookMessageBuilderToId(new WebhookMessageBuilder().addEmbed(embed).setDisplayName(api.getYourself().getName()).setDisplayAvatar(api.getYourself().getAvatar()), id, api);
    }

    private static EmbedBuilder getUserUpdatedEmbedBuilder(User user) {
        return new EmbedBuilder().setTitle(((UserImpl) user).toString() + " wurde aktualisiert.").setFooter(user.getDiscriminatedName(), user.getAvatar()).setColor(user.getRoleColor(user.getApi().getServerById(AN).orElse(null)).orElse(Color.BLACK)).setTimestampToNow();
    }

    private void userChangedName(UserChangeNameEvent event) {
        userChangedDiscriminatedName(event.getUser(), event.getOldName() + "#" + event.getUser().getDiscriminator(), event.getNewName() + "#" + event.getUser().getDiscriminator());
    }

    private void userChangedDiscriminator(UserChangeDiscriminatorEvent event) {
        userChangedDiscriminatedName(event.getUser(), event.getUser().getName() + "#" + event.getOldDiscriminator(), event.getUser().getName() + "#" + event.getNewDiscriminator());
    }

    private static void userChangedDiscriminatedName(User user, String oldName, String newName) {
        sendEmbedToLogs(getUserUpdatedEmbedBuilder(user)
                .addField("Name:", xToY(oldName, newName))
                , user.getApi());
    }

    private void userChangedNickname(UserChangeNicknameEvent event) {
        sendEmbedToLogs(getUserUpdatedEmbedBuilder(event.getUser())
                        .addField("Nickname:", xToY(event.getOldNickname().orElse(event.getUser().getName()), event.getNewNickname().orElse(event.getUser().getName())))
                , event.getUser().getApi());
    }

    private void userChangedAvatar(UserChangeAvatarEvent event) {
        sendEmbedToLogs(getUserUpdatedEmbedBuilder(event.getUser())
                        .setImage(event.getNewAvatar())
                        .setFooter(event.getUser().getDiscriminatedName(), event.getOldAvatar())
                        .addField("Avatar:", getHyperLink(event.getOldAvatar().getUrl().toString(), "Alt") + " -> " + getHyperLink(event.getNewAvatar().getUrl().toString(), "Neu"))
                , event.getUser().getApi());
    }

    private static String getHyperLink (String url, String text) {
        return String.format("[%s](%s)", text, url);
    }

    private static String xToY(String x, String y) {
        return "`" + x + "` -> `" + y + "`";
    }

    private void roleChangedPermission(RoleChangePermissionsEvent event) {
        if (event.getRole().isManaged()) return;

        long role = event.getRole().getId();
        if (event.getRole().isEveryoneRole()) {
            updatePermissions(event, 104154176); //https://discordapi.com/permissions.html#104154176 old:70321344
        } else if (role == BOT_MITGLIED) {
            updatePermissions(event, 804773313); //https://discordapi.com/permissions.html#804773313
        } else if (role != MITGLIED && role != ROLLENMEISTER) {
            if (event.getRole().getName().toLowerCase().contains("mitglied")) { //vorläufiges Mitglied, rest braucht die Rechte nicht
                updatePermissions(event, 267771329); //https://discordapi.com/permissions.html#267771329
            } else {
                updatePermissions(event, 0);
            }
        }
    }

    private static void updatePermissions(RoleChangePermissionsEvent event, int bitmask) {
        if (event.getNewPermissions().getAllowedBitmask() == bitmask) return;

        event.getRole().updatePermissions(Permissions.fromBitmask(bitmask)).exceptionally(ExceptionLogger.get());
    }

    private void userAddedRole(UserRoleAddEvent event) {
        if (event.getRole().getId() == MITGLIED) {
            event.getUser().sendMessage("Herzlichen Glückwunsch. Du bist nun Mitglied auf der Gilde des Asozialen Netzwerkes.\nFolgende Sachen sind verboten:\n - @everyone, @here, @Mitglied oder @Vorläufiges Mitglied markieren\n - Ein Arschloch sein.\n - Kanäle löschen\n - Scheiße bauen.").exceptionally(ExceptionLogger.get());
            event.getServer().getTextChannelById(LOGS).ifPresent(channel -> channel.sendMessage(event.getUser().getIdAsString() + " ist nun Mitglied.").exceptionally(ExceptionLogger.get()));
        }
    }

    private void checkMessageBadStuff(Message m) {
        m.getAuthor().asUser().ifPresent(u -> {
            api.getRoleById(MITGLIED).ifPresent(mitglied -> {
                if (mitglied.hasUser(u)) {
                    if (m.mentionsEveryone()) {
                        removeMitglied(mitglied, u, m.getChannel(), "Hat alle markiert: " + m.getLink());
                    } else if (m.getContent().contains(mitglied.getMentionTag())) {
                        removeMitglied(mitglied, u, m.getChannel(), "Hat alle Mitglieder markiert: " + m.getLink());
                    } else if (m.getContent().contains("<@" + VORLÄUFIG + ">")) {
                        removeMitglied(mitglied, u, m.getChannel(), "Hat alle Vorläufigen markiert: " + m.getLink());
                    }
                }
            });
        });
    }

    private void removeMitglied(Role mitglied, User u, TextChannel c, String reason) {
        u.removeRole(mitglied, reason).exceptionally(ExceptionLogger.get()).thenAccept((v) -> {
            c.sendMessage(u.getMentionTag() + ", dir wurde die Mitgliedsrolle entzogen, wenn du glaubst, dass es ein Fehler war, beschwere dich bitte ohne Leute zu markieren.").exceptionally(ExceptionLogger.get());
            api.getTextChannelById(LOGS).ifPresent(channel -> channel.sendMessage(u.getIdAsString() + " ist nun kein Mitglied mehr. Grund: " + reason).exceptionally(ExceptionLogger.get()));
        });

        api.getRoleById(VORLÄUFIG).ifPresent(r -> {
            if (!r.hasUser(u)) u.addRole(r, "Mitglied weg.").exceptionally(ExceptionLogger.get());
        });
    }

    private void messageCreated(MessageCreateEvent event) {
        //delete meessages in #witzig
        if (event.getChannel().getId() == WITZIG_KANAL && event.getMessageAuthor().isUser()) {
            event.getMessage().delete();
            return;
        }

        if (event.getMessageContent().isEmpty() && event.getMessage().getEmbeds().isEmpty() && event.getMessageAttachments().isEmpty()) return;

        checkNoRoleUsers();

        checkMessageBadStuff(event.getMessage());

        //reminds to bump
        if (event.getMessageAuthor().getId() == 302050872383242240L //is disboard bot
            && event.getMessage().getEmbeds().size() == 1) { //message has embed
            Embed embed = event.getMessage().getEmbeds().get(0);
            embed.getDescription().ifPresent(description -> {
                if (description.contains("Bump erfolgreich")) {
                    event.getApi().getThreadPool().getScheduler().schedule(() -> {
                        event.getChannel().getCurrentCachedInstance().ifPresent(channel -> {
                            channel.sendMessage("<@&763147436825772042>!").exceptionally(ExceptionLogger.get());
                        });
                    }, 2, TimeUnit.HOURS);
                }
            });
        }

        func.getIncomingWebhook(cloneTextchannel(event.getServerTextChannel().orElse(null))).ifPresent(webhook -> event.getMessage()
                            .toWebhookMessageBuilder()
                            .setDisplayName(event.getMessageAuthor().getDisplayName() + " (" + event.getMessageAuthor().getId() + ")")
                            .setAllowedMentions(new AllowedMentionsBuilder().build())
                            .send(webhook).exceptionally(ExceptionLogger.get())
        );

        if (event.getChannel().getId() == PETITIONEN_KANAL
                && event.getMessageAuthor().getId() != LAWLIET_ID
                && !func.WHITE_SPACE.matcher(event.getMessageContent()).replaceAll("").toLowerCase().startsWith("l.vote")) {
            func.getIncomingWebhook(event.getApi().getServerTextChannelById(681650503351795766L).orElse(null)).ifPresent(webhook -> event.getMessage()
                    .toWebhookMessageBuilder()
                    .setAllowedMentions(new AllowedMentionsBuilder().setMentionEveryoneAndHere(false).setMentionRoles(false).build())
                    .send(webhook).exceptionally(ExceptionLogger.get())
            );
            event.getMessage().delete().exceptionally(ExceptionLogger.get());
            return;
        }

        if(!event.getMessageAuthor().isRegularUser()) return;

        User user = event.getMessageAuthor().asUser().orElse(null);
        Server server = event.getServer().orElse(null);
        if (user != null && server != null && user.getRoles(server).size() == 1 /*@everyone role*/) {
            if (newUsersLastMessage.containsKey(user.getId()) && isMoreThan10minAgo(newUsersLastMessage.get(user.getId()))) {
                    newUsersLastMessage.remove(user.getId());
            }
            if (newUsersLastMessage.containsKey(user.getId()) && user.getJoinedAtTimestamp(server).map(ts -> ts.toEpochMilli() > newUsersLastMessage.get(user.getId())).orElse(false)) {
                newUsersLastMessage.remove(user.getId());
            }
            String replacedContent = event.getMessageContent().replaceAll("[,.]", "");
            if (replacedContent.equals(Integer.toString(calculateCaptchaNumber(user, server)))) {
                try {
                    newUsersLastMessage.remove(user.getId());
                    user.addRole(server.getRoleById(559141475812769793L).orElseThrow(() -> new AssertionError("Rolle nicht da"))).join();
                    //user.addRole(server.getRoleById(559444155726823484L).orElseThrow(() -> new AssertionError("Rolle nicht da"))).join();
                    event.getChannel().sendMessage(user.getNicknameMentionTag() + " du kannst dir nun in <#686282295098736820> Rollen geben ;)").exceptionally(ExceptionLogger.get());
                } catch (Exception e) {
                    func.handle(e);
                }
            } else if (newUsersLastMessage.containsKey(user.getId())) { //got captcha
                if (replacedContent.contains(Integer.toString(calculateCaptchaNumber(user, server)))) {
                    event.getChannel().sendMessage(user.getDisplayName(server) + " du musst die Zahl alleine schreiben. :)");
                } else {
                    try {
                        int answer = calculateCaptchaNumber(user, server);
                        int sent = Integer.parseInt(replacedContent);
                        if (sent > answer) {
                            event.getMessage().addReaction("⬇️").exceptionally(ExceptionLogger.get());
                        } else {
                            event.getMessage().addReaction("⬆️").exceptionally(ExceptionLogger.get());
                        }

                        int answerLength = Integer.toString(answer).length();

                        if (answerLength == replacedContent.length()) {

                            String sentStr = NumberToText.intToText(sent);
                            String answerStr = NumberToText.intToText(answer);

                            String difference = StringUtils.difference(sentStr, answerStr);

                            String reverseAnswerStr = StringUtils.reverse(answerStr);
                            if (difference.equals(answerStr)) {
                                String reverseSentStr = StringUtils.reverse(sentStr);

                                String reverseDifference = StringUtils.difference(reverseSentStr, reverseAnswerStr);

                                if (reverseDifference.equals(reverseAnswerStr)) {
                                    new MessageBuilder().setContent(user.getNicknameMentionTag() + " die Zahl lautet: " + answerStr)
                                            .send(event.getChannel()).exceptionally(ExceptionLogger.get());
                                } else {
                                    String reverseDifferenceReversed = StringUtils.reverse(reverseDifference);
                                    new MessageBuilder().setContent(user.getNicknameMentionTag() + " " + answerStr.replaceFirst(reverseDifferenceReversed, "***" + reverseDifferenceReversed.toUpperCase() + "***"))
                                            .send(event.getChannel()).exceptionally(ExceptionLogger.get());
                                }
                            } else {
                                String differenceReversed = StringUtils.reverse(difference);
                                new MessageBuilder().setContent(user.getNicknameMentionTag() + " " + StringUtils.reverse(reverseAnswerStr.replaceFirst(differenceReversed, "***" + differenceReversed.toUpperCase() + "***")))
                                        .send(event.getChannel()).exceptionally(ExceptionLogger.get());
                            }
                        } else { //length differs
                            new MessageBuilder().setContent(user.getNicknameMentionTag() + " die Zahl ist " + answerLength + " Ziffern lang.")
                                    .send(event.getChannel()).exceptionally(ExceptionLogger.get());
                        }
                        return;
                    } catch (NumberFormatException ignored) {}
                }
                event.getMessage().addReaction("❌").exceptionally(ExceptionLogger.get());
            } else if (!DiscordRegexPattern.ROLE_MENTION.matcher(event.getMessageContent()).find()
                        && !event.getMessageContent().contains("@everyone")
                        && !event.getMessageContent().contains("@here")
                        && event.getMessage().getMentionedUsers().size() < 2) {
                    sendCaptcha(user, server, event.getChannel());
            }
        }
    }

    private static String getCaptchaDescription(User user, Server server) {
        return user.getNicknameMentionTag() + " schreibe \"" + NumberToText.intToText(calculateCaptchaNumber(user, server)) + "\" mit Ziffern (0-9) in diesen Kanal.";
    }

    private boolean isMoreThan10minAgo(long timeMs) {
        return (System.currentTimeMillis() - timeMs) > 10 * 60 * 1000;
    }

    private void sendCaptcha(User user, Server server, TextChannel channel) {
        if (channel == null || user.getRoles(server).size() > 1) return;

        if (!newUsersLastMessage.containsKey(user.getId())
                || isMoreThan10minAgo(newUsersLastMessage.get(user.getId()))) { //is 10 min ago last message

            newUsersLastMessage.put(user.getId(), System.currentTimeMillis());
            EmbedBuilder embed = func.getNormalEmbed(user, null)
                    .setTitle("Captcha")
                    .setDescription(getCaptchaDescription(user, server));

            channel.sendMessage(embed).exceptionally(ExceptionLogger.get());
        }
    }

    public static int calculateCaptchaNumber(User user, Server server) {
        int hash = Objects.hash(user.getJoinedAtTimestamp(server).orElse(Instant.EPOCH), user.getIdAsString() + ":why_so_salty#LazyCrypto");
        return (Math.abs(hash) % 900000) + 100000;
    }

    private static ServerTextChannel cloneTextchannel(ServerTextChannel channel) {
        if (channel == null) {
            return null;
        }
        
        Server copy = channel.getApi().getServerById(COPY).orElse(null);
        if (copy == null) return null;
        ServerTextChannel stc = copy.getTextChannelsByName(channel.getIdAsString()).stream().findAny().orElse(null);
        String newTopic = channel.getMentionTag() + "\n" + channel.getTopic();
        if (newTopic.length() > 1024) {
            newTopic = newTopic.substring(0, 1023);
        }
        if (stc == null) {
            stc = new ServerTextChannelBuilder(copy)
                    .setName(channel.getIdAsString())
                    .setSlowmodeDelayInSeconds(channel.getSlowmodeDelayInSeconds())
                    .setTopic(newTopic).create().join();
        } else {
            if (!stc.getTopic().equals(newTopic)
                    || stc.getSlowmodeDelayInSeconds() != channel.getSlowmodeDelayInSeconds()
                    || stc.isNsfw() != channel.isNsfw()) {
                stc.createUpdater()
                        .setSlowmodeDelayInSeconds(channel.getSlowmodeDelayInSeconds())
                        .setNsfwFlag(channel.isNsfw())
                        .setTopic(newTopic)
                        .update().exceptionally(ExceptionLogger.get());
            }
        }
        return stc;
    }

    private void memberBanned(ServerMemberBanEvent event) {
        if (func.userIsTrusted(event.getUser())) {
            event.getServer().unbanUser(event.getUser());
        }
    }

    private void channelDeleted(ServerChannelDeleteEvent event) {
        try {
            event.getServer().getAuditLog(1, AuditLogActionType.CHANNEL_DELETE).join().getInvolvedUsers().forEach(user1 -> {
                user1.removeRole(func.getApi().getRoleById(MITGLIED).orElseThrow(() -> new AssertionError("Mitgliedsrolle nicht da")), "Channel got deleted").join();
                event.getServer().getOwner().ifPresent(owner ->
                        owner.sendMessage(user1.getDisplayName(event.getServer()) + " (name: " + user1.getDiscriminatedName() + "; id: " + user1.getIdAsString() + ") hat #" + event.getChannel().getName() + " gelöscht.").join()
                );

            });
        } catch (Exception e) {
            func.handle(e);
        }
    }

    //Fortnite-Detektor
    private void activityChanged(UserChangeActivityEvent event) {
        if (event.getUser().isEmpty() || event.getUser().get().isBot()) return;
        User user = event.getUser().get();

        event.getApi().getServerTextChannelById(623940807619248148L).ifPresent(textchannel -> {
    		event.getNewActivity().ifPresent(activity -> {
    			event.getOldActivity().ifPresent(oldactivity -> {
    				if (!oldactivity.getApplicationId().orElse(0L).equals(activity.getApplicationId().orElse(1L))
                            && ((activity.getType().equals(ActivityType.PLAYING)
                            && activity.getName().equals("Fortnite"))
                            || (activity.getApplicationId().orElse(0L) == 432980957394370572L))) {
    					EmbedBuilder embed = new EmbedBuilder()
    							.setColor(Color.RED)
    							.setTimestampToNow()
    							.setFooter(user.getDiscriminatedName(), user.getAvatar());
    					activity.getDetails().ifPresent(string -> {
    						embed.addField("\u200B", "\nDetails: (" + string + ")");
    					});
    					textchannel.sendMessage(embed.setDescription(user.getNicknameMentionTag() + " spielt " + activity.getName() + "."));
    					func.getApi().getRoleById(623193804551487488L).ifPresent(user::addRole);
    				}
    			});
    		});
    	});

        EmbedBuilder embed = getUserUpdatedEmbedBuilder(user);
        if (event.getNewActivity().isPresent()) {
            String n = getActivity(event.getNewActivity().get());
            String o = getActivity(event.getOldActivity().orElse(null));
            if (!o.equals(n)) {
                sendEmbedToId(embed.addField("Activity:", o + " -> " + n), ACTIVITY_LOGS, event.getApi());
            }
        }
    }

    private static String getActivity(Activity activity) {
        if (activity == null) return "none";

        String type;
        switch (activity.getType()) {
            case CUSTOM:
                if (activity.getState().isPresent() || activity.getEmoji().isPresent()) {
                    return  (activity.getEmoji().isPresent() ? activity.getEmoji().get().getMentionTag() : "")
                            + (activity.getEmoji().isPresent() && activity.getState().isPresent() ? " " : "")
                            + (activity.getState().isPresent() ? "`" + activity.getState().get() + "`" : "");
                }
            case STREAMING:
                return "`Streamt " + activity.getStreamingUrl().orElse(activity.getName()) + "`";
            case WATCHING:
                type = "`Schaut %s`";
                break;
            case LISTENING:
                type = "`Hört %s zu`";
                break;
            default:
                type = "`Spielt %s`";
        }
        return String.format(type, activity.getName());
    }

    private long lastChecked = 0L;
    private void checkNoRoleUsers() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastChecked > 69 * 60 * 1000) { //69 minutes
            lastChecked = currentTime;

            Zitat.updateQuotes();

            api.getServerById(AN).ifPresent(server -> {
                LinkedList<User> users = new LinkedList<>();
                for (User u : server.getMembers()) {
                    if (u.getRoles(server).size() == 1) {
                        long joinedAt = u.getJoinedAtTimestamp(server).map(Instant::toEpochMilli).orElse(-1L);
                        if (joinedAt != -1L && currentTime - joinedAt > 14 * 24 * 60 * 60 * 1000) { //two weeks
                            users.add(u);
                        }
                    }
                }
                System.out.println(Instant.ofEpochMilli(currentTime).toString()
                        + " checked no roles. There are "
                        + users.size() + " without role longer than two weeks."); //DEBUG

                if (users.size() == 0) {
                    return;
                }

                WebhookMessageBuilder wmb = new WebhookMessageBuilder();
                wmb.setDisplayAvatar(api.getYourself().getAvatar());
                wmb.setDisplayName(api.getYourself().getName());
                StringBuilder sb = new StringBuilder();
                for (User user : users) {
                    String toAppend = user.getDiscriminatedName() + " (id: " + user.getId() + ")\n";
                    String sbStr = sb.toString();
                    if (sbStr.length() + toAppend.length() > 2048) {
                        wmb.addEmbed(new EmbedBuilder().setDescription(sbStr));
                        sb = new StringBuilder();
                    }

                    sb.append(toAppend);

                    user.sendMessage("Du wurdest vom Discord-Server des Asozialen Netzwerkes gekickt,"
                            + " da du es in zwei Wochen nicht geschafft hast "
                            + calculateCaptchaNumber(user, server)
                            + " zu schreiben. Nutze folgenden Link um erneut beizutreten:"
                            + " https://asozialesnetzwerk.github.io/discord")
                            .exceptionally(ExceptionLogger.get()) //on error sets message to null
                            .thenCompose(message -> {
                                System.out.println("Kicked " + user + (message == null ? ", but couldn't send a message" : " and send them a message"));
                                return server.kickUser(user, "Ist zwei Wochen hier und hat keine Rolle.");
                            }).exceptionally(ExceptionLogger.get());
                }
                String sbStr = sb.toString();
                if (!sbStr.isEmpty()) {
                    wmb.addEmbed(new EmbedBuilder().setDescription(sbStr));
                }
                wmb.setContent("kicked " + users.size() + " users.");
                sendWebhookMessageBuilderToId(wmb, LOGS, api);
            });
        }
    }
}
