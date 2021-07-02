package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Talon extends Flippable {

    public Talon() {
    }

    public Talon(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.TALON, addCrimped);

        for (JSONArray arr : pinDefs) {
            Sprite temp;
            if (connectors.size() == connNum) {
                break;
            }
            temp = new Sprite(new Texture(Gdx.files.internal("img/point.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            temp.setSize((Long) pinSizeDefs.get(pinDefs.indexOf(arr)).get(0), (Long) pinSizeDefs.get(pinDefs.indexOf(arr)).get(1));
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    public Vector2 calculate(int port) {
        if (port >= 2 && port < 4) {
            return calculateDirection(cur, port);
        } else {
            return calculateDirection(2 + cur, port);
        }
    }

    public String getCAN(Cable c1, Cable c2) {
        if (c1 == get(7) && c2 == get(6)) {
            if (getNull(5) || getNull(4)) {
                return "CAN chain ends at Talon " + hardwareID2;
            } else if (getOther(4) instanceof PowerDistributionPanel && getOther(5) instanceof PowerDistributionPanel) {
                if ((getNum(4) == 9 && getNum(5) == 8) || (getNum(4) == 7 && getNum(5) == 6)) {
                    return "PDP";
                } else {
                    return "Incorrectly wired to PDP";
                }
            } else if (getOther(4) instanceof PneumaticsControlModule && getOther(5) instanceof PneumaticsControlModule) {
                if ((getNum(4) == 5 && getNum(5) == 4) || (getNum(4) == 3 && getNum(5) == 2)) {
                    return "PCM";
                }
                return "Incorrectly wired to PCM";
            } else if (getOther(4) instanceof RoboRio && getOther(5) instanceof RoboRio) {
                if (getNum(4) == 2 && getNum(5) == 3) {
                    return "RoboRIO";
                }
                return "Incorrectly wired to RoboRIO";
            } else if (!(((getOther(5) instanceof Talon && getOther(4) instanceof Talon) || (getOther(5) instanceof Spark && getOther(4) instanceof Spark) || (getOther(5) instanceof Falcon && getOther(4) instanceof Falcon)) && getONum(4) == getONum(5))) {
                return "CAN chain improperly wired at Talon " + hardwareID2;
            } else {
                return getOther(5).getCAN(get(5), get(4));
            }
        } else if (c1 == get(5) && c2 == get(4)) {
            if (getNull(7) || getNull(6)) {
                return "CAN chain ends at Talon " + hardwareID2;
            } else if (getOther(6) instanceof PowerDistributionPanel && getOther(7) instanceof PowerDistributionPanel) {
                if ((getNum(6) == 9 && getNum(7) == 8) || (getNum(6) == 7 && getNum(7) == 6)) {
                    return "PDP";
                }
                return "Incorrectly wired to PDP";
            } else if (getOther(6) instanceof PneumaticsControlModule && getOther(7) instanceof PneumaticsControlModule) {
                if ((getNum(6) == 5 && getNum(7) == 4) || (getNum(6) == 3 && getNum(7) == 2)) {
                    return "PCM";
                }
                return "Incorrectly wired to PCM";
            } else if (getOther(6) instanceof RoboRio && getOther(7) instanceof RoboRio) {
                if (getNum(6) == 2 && getNum(7) == 3) {
                    return "RoboRIO";
                }
                return "Incorrectly wired to RoboRIO";
            } else if (!(((getOther(7) instanceof Talon && getOther(6) instanceof Talon) || (getOther(7) instanceof Spark && getOther(6) instanceof Spark) || (getOther(7) instanceof Falcon && getOther(6) instanceof Falcon)) && getONum(6) == getONum(7))) {
                return "CAN chain improperly wired at Talon " + hardwareID2;
            } else {
                return getOther(7).getCAN(get(7), get(6));
            }
        } else {
            return "CAN chain improperly wired at Talon " + hardwareID2;
        }
    }

    public String check() {

        if (getNull(0) || getNull(1) || !(getOther(0) instanceof PowerDistributionPanel && getOther(1) instanceof PowerDistributionPanel)) {
            resetLedBad();
            return "Talon is not connected to PDP";
        }

        if (getNum(1) <= 41 && getNum(1) >= 34) {
            if (getNum(1) % 2 != 1 || getNum(0) != getNum(1) - 1) {
                resetLedBad();
                return "Talon incorrectly connected to PDP";
            }
        }

        if (getNum(1) <= 17 && getNum(1) >= 10) {
            if (getNum(1) % 2 != 1 || getNum(0) != getNum(1) - 1) {
                resetLedBad();
                return "Talon incorrectly connected to PDP";
            }
        }

        if (getNum(1) <= 33 && getNum(1) >= 26) {
            if (getNum(1) % 2 != 0 || getNum(0) != getNum(1) + 1) {
                resetLedBad();
                return "Talon incorrectly connected to PDP";
            }
        }

        if (getNum(1) <= 25 && getNum(1) >= 18) {
            if (getNum(1) % 2 != 0 || getNum(0) != getNum(1) + 1) {
                resetLedBad();
                return "Talon incorrectly connected to PDP";
            }
        }

        simLedBad();

        if (getAllNull(4, 7)) {
            simLedBad();
            return "Talon not connected to CAN chain";
        }

        if (!((getCAN(get(5), get(4)).equals("PDP") && getCAN(get(7), get(6)).equals("PCM")) ||
                (getCAN(get(5), get(4)).equals("PCM") && getCAN(get(7), get(6)).equals("PDP"))) &&
                !((getCAN(get(5), get(4)).equals("PDP") && getCAN(get(7), get(6)).equals("RoboRIO")) ||
                        (getCAN(get(5), get(4)).equals("RoboRIO") && getCAN(get(7), get(6)).equals("PDP")))
        ) {


            String ans = "";

            if (!getCAN(get(5), get(4)).equals("PDP") && !getCAN(get(5), get(4)).equals("PCM") && !getCAN(get(5), get(4)).equals("RoboRIO")) {
                ans += getCAN(get(5), get(4));
            } else {
                if (getCAN(get(5), get(4)).equals("PDP")) {
                    ans += "CAN chain must reach PCM or RoboRIO";
                } else {
                    ans += "CAN chain must reach PDP";
                }
            }

            if (!getCAN(get(7), get(6)).equals("PDP") && !getCAN(get(7), get(6)).equals("PCM") && !getCAN(get(7), get(6)).equals("RoboRIO")) {
                if (!getCAN(get(7), get(6)).equals(ans)) {
                    if (ans.length() > 0) {
                        ans += "\n";
                    }
                    ans += getCAN(get(7), get(6));
                }
            } else {
                if (getCAN(get(7), get(6)).equals("PDP")) {
                    if (!ans.equals("CAN chain must reach PCM or RoboRIO")) {
                        if (ans.length() > 0) {
                            ans += "\n";
                        }
                        ans += "CAN chain must reach PCM or RoboRIO";
                    }
                } else {
                    if (!ans.equals("CAN chain must reach PDP")) {
                        if (ans.length() > 0) {
                            ans += "\n";
                        }
                        ans += "CAN chain must reach PDP";
                    }
                }
            }
            simLedBad();

            return ans;
        }

        if (!simLED || LEDs.get(0).getColor().equals("red")) {
            simLED = true;
            LEDs.get(0).blink(20);
            LEDs.get(1).blink(20);
            LEDs.get(0).setColor("green");
            LEDs.get(0).setStatus("Enabled");
            LEDs.get(1).setColor("green");
            LEDs.get(1).setStatus("Enabled");
        }


        return null;
    }

    private void resetLedBad() {
        if (simLED) {
            resetLEDs();
            simLED = false;
        }
    }

    private void simLedBad() {
        if (!simLED || LEDs.get(0).getColor().equals("green")) {
            simLED = true;
            LEDs.get(0).blink(20);
            LEDs.get(1).blink(20);
            LEDs.get(0).setColor("red");
            LEDs.get(0).setStatus("CAN chain error");
            LEDs.get(1).setColor("red");
            LEDs.get(1).setStatus("CAN chain error");
        }
    }

}