package com.buam.ultimatesigns.lang;

import com.buam.ultimatesigns.SignHelper;
import com.buam.ultimatesigns.USign;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.lang.exceptions.InvalidArgumentsException;
import com.buam.ultimatesigns.lang.types.Number;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Language {

    private String[] lines;
    private int currentLine;
    private boolean left;

    // a is for for (iterations)
    private int a;
    // b is for for (start index of for)
    private int b;
    // c is for for (end index of for)
    private int c;
    private String d;
    private String e;

    public Language(String[] lines, boolean left) {
        this.lines = lines;
        this.left = left;
    }

    public void executeAll(Player p, USign s) throws InvalidArgumentsException, IllegalAccessException, InstantiationException {
        execute(p, s);
    }

    private void execute(Player p, USign s) throws InvalidArgumentsException, InstantiationException, IllegalAccessException {
        if(lines.length == 0) return;
        // FOR
        if(a != 0) {
            if(currentLine == c) {
                if(a != 1) {
                    currentLine = b;
                }
                a--;
            }
        }
        // ENDFOR

        String cmd = lines[currentLine];

        boolean exec = true;

        if(cmd.contains("(left)")) {
            if(!left) exec = false;
            cmd = cmd.replace("(left)", "").trim();
        }
        if(cmd.contains("(right)")) {
            if(left) exec = false;
            cmd = cmd.replace("(right)", "").trim();
        }

        int delayMillis = 0;

        if(!cmd.trim().equalsIgnoreCase("") && exec) {

            // Execute
            if(a != 0) {
                // Index variable in for loop
                cmd = cmd.replace("[i]", Integer.toString(a));
            }
            if (cmd.contains("(msg")) {
                cmd = cmd.replace("(msg", "");
                cmd = cmd.replace(")", "");
                cmd = cmd.trim();

                String player = "";
                String message = "";

                player = cmd.substring(0, cmd.indexOf('"')).trim();
                message = cmd.substring(cmd.indexOf('"')).replace("\"", "").trim();

                if (TypeManager.isText(player)) {
                    player = TypeManager.getText(player, p, s);
                }
                message = SignHelper.translateColors(TypeManager.translate(message, p, s));

                Player messageTo = Bukkit.getServer().getPlayer(player);
                if (messageTo != null) {
                    messageTo.sendMessage(message);
                } else {
                    p.sendMessage(ChatColor.RED + "Player " + player + " does not exist");
                }
            } else if(cmd.contains("(delay")) {
                cmd = cmd.replace("(delay", "");
                cmd = cmd.replace(")", "");
                cmd = cmd.trim();

                try {
                    delayMillis = Integer.parseInt(cmd);
                } catch(NumberFormatException e) {
                    throw new InvalidArgumentsException("Not a number: " + cmd);
                }
            } else if (cmd.contains("(goto")) {
                cmd = cmd.replace("(goto", "");
                cmd = cmd.replace(")", "");
                cmd = cmd.trim();
                try {
                    int newLine = Integer.parseInt(cmd);
                    // -2 so next command is the right one (since one gets added at the end here and were starting from 1)
                    currentLine = newLine - 2;
                    if (newLine - 1 > lines.length) {
                        throw new InvalidArgumentsException("Number too big: " + newLine);
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidArgumentsException("Not a Number: " + cmd);
                }
            } else if (cmd.contains("(for")) {
                cmd = cmd.replace("(for", "");
                cmd = cmd.replace(")", "");
                cmd = cmd.trim();

                int num = 0;

                if (TypeManager.isNumber(cmd)) {
                    num = TypeManager.getNumber(cmd, p, s);
                } else {
                    try {
                        num = Integer.parseInt(cmd);
                    } catch (NumberFormatException e) {
                        throw new InvalidArgumentsException("Not a Number: " + cmd);
                    }
                }

                a = num;
                b = currentLine + 1;
                c = findNextEndFor();

                if (c == 0) throw new InvalidArgumentsException("For-loop has no (endfor)");

            } else if (cmd.contains("(if")) {
                cmd = cmd.replace("(if", "");
                cmd = cmd.replace(")", "");
                cmd = cmd.replace(" ", "");

                if (!TypeManager.getIf(cmd, p, s)) {
                    currentLine = findNextEndIf() + 2;
                }
            } else {
                if (!cmd.contains("(endfor)") && !cmd.contains("(endif)")) executeCommand(cmd, p, s);
            }
        }

        currentLine++;
        if(lines.length == currentLine) return;
        // Schedule task with specific delay and convert the delay from milliseconds to ticks
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> {
            try {
                execute(p, s);
            } catch (InvalidArgumentsException | InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }, delayMillis / 1000 * 20);
    }

    private void executeCommand(String cmd, Player p, USign s) throws InstantiationException, IllegalAccessException {
        cmd = TypeManager.translate(cmd, p, s);
        if(cmd.contains("(console)")) {
            cmd = cmd.replace("(console)", "").trim();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> UltimateSigns.i.messagesBlocked.remove(p), 1);
            return;
        }
        if(cmd.contains("(silent)")) {
            cmd = cmd.replace("(silent)", "").trim();
            UltimateSigns.i.messagesBlocked.add(p);
        }
        p.performCommand(cmd);
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> UltimateSigns.i.messagesBlocked.remove(p), 1);
    }

    private int findNextEndFor() {
        for(int i = currentLine; i<lines.length; i++) {
            if(lines[i].trim().equalsIgnoreCase("(endfor)")) {
                return i;
            }
        }
        return 0;
    }

    private int findNextEndIf() {
        for(int i = currentLine; i< lines.length; i++) {
            if(lines[i].trim().equalsIgnoreCase("(endif")) {
                return i;
            }
        }
        return 0;
    }

    public int getCurrentLine() {
        return currentLine;
    }

}
