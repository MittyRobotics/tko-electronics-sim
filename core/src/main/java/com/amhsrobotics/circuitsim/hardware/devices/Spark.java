package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Spark extends Flippable {

    public Spark() {}

    public Spark(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.SPARK, addCrimped);


        for(JSONArray arr : pinDefs) {
            Sprite temp;
            if(connectors.size() == connNum) {
                break;
            }
            temp = new Sprite(new Texture(Gdx.files.internal("img/point.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            temp.setSize((Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(0), (Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(1));
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    public Vector2 calculate(int port) {
        if(port < 3) {
            return calculateDirection(cur+2, port);
        } else if (port < 5) {
            return calculateDirection(cur, port);
        } else {
            return calculateDirection(cur+3, port);
        }
    }

    public String getCAN(Cable c1, Cable c2) {
        if(c1 == get(8) && c2 == get(7)) {
            if(getNull(6) || getNull(5)) {
                return "CAN chain ends at SPARK " + hardwareID2;
            } else if (getOther(5) instanceof PowerDistributionPanel && getOther(6) instanceof PowerDistributionPanel) {
                if((getNum(5) == 9 && getNum(6) == 8) || (getNum(5) == 7 && getNum(6) == 6)) {
                    return "PDP";
                } else {
                    return "Incorrectly wired to PDP";
                }
            } else if (getOther(5) instanceof PneumaticsControlModule && getOther(6) instanceof PneumaticsControlModule) {
                if ((getNum(5) == 5 && getNum(6) == 4) || (getNum(5) == 3 && getNum(6) == 2)) {
                    return "PCM";
                }
                return "Incorrectly wired to PCM";
            } else if (getOther(5) instanceof RoboRio && getOther(6) instanceof RoboRio) {
                if(getNum(5) == 2 && getNum(6) == 3) {
                    return "RoboRIO";
                }
                return "Incorrectly wired to RoboRIO";
            } else if (!(((getOther(6) instanceof Talon && getOther(5) instanceof Talon) || (getOther(6) instanceof Spark && getOther(4) instanceof Spark) || (getOther(6) instanceof Falcon && getOther(5) instanceof Falcon)) && getONum(5) == getONum(6))) {
                return "CAN chain improperly wired at SPARK " + hardwareID2;
            } else {
                return getOther(6).getCAN(get(6), get(5));
            }
        } else if (c1 == get(6) && c2 == get(5)) {
            if(getNull(8) || getNull(7)) {
                return "CAN chain ends at SPARK " + hardwareID2;
            } else if (getOther(7) instanceof PowerDistributionPanel && getOther(8) instanceof PowerDistributionPanel) {
                if((getNum(7) == 9 && getNum(8) == 8) || (getNum(7) == 7 && getNum(8) == 6)) {
                    return "PDP";
                }
                return "Incorrectly wired to PDP";
            } else if (getOther(7) instanceof PneumaticsControlModule && getOther(8) instanceof PneumaticsControlModule) {
                if ((getNum(7) == 5 && getNum(8) == 4) || (getNum(7) == 3 && getNum(8) == 2)) {
                    return "PCM";
                }
                return "Incorrectly wired to PCM";
            } else if (getOther(7) instanceof RoboRio && getOther(8) instanceof RoboRio) {
                if(getNum(7) == 2 && getNum(8) == 3) {
                    return "RoboRIO";
                }
                return "Incorrectly wired to RoboRIO";
            } else if (!(((getOther(8) instanceof Talon && getOther(7) instanceof Talon) || (getOther(8) instanceof Spark && getOther(7) instanceof Spark) || (getOther(8) instanceof Falcon && getOther(7) instanceof Falcon)) && getONum(7) == getONum(8))) {
                return "CAN chain improperly wired at SPARK " + hardwareID2;
            } else {
                return getOther(8).getCAN(get(8), get(7));
            }
        } else {
            return "CAN chain improperly wired at SPARK " + hardwareID2;
        }
    }

    public String check() {
        if(getNull(4) || getNull(3) || !(getOther(4) instanceof PowerDistributionPanel && getOther(3) instanceof PowerDistributionPanel)) {
            resetLedBad();
            return "SPARK is not connected to PDP";
        }

        if(getNum(3) <= 41 && getNum(3) >= 34) {
            if(getNum(3) % 2 != 1 || getNum(4) != getNum(3) - 1) {
                resetLedBad();
                return "SPARK incorrectly connected to PDP";
            }
        }

        if(getNum(3) <= 17 && getNum(3) >= 10) {
            if(getNum(3) % 2 != 1 || getNum(4) != getNum(3) - 1) {
                resetLedBad();
                return "SPARK incorrectly connected to PDP";
            }
        }

        if(getNum(3) <= 33 && getNum(3) >= 26) {
            if(getNum(3) % 2 != 0 || getNum(4) != getNum(3) + 1) {
                resetLedBad();
                return "SPARK incorrectly connected to PDP";
            }
        }

        if(getNum(3) <= 25 && getNum(3) >= 18) {
            if(getNum(3) % 2 != 0 || getNum(4) != getNum(3) + 1) {
                resetLedBad();
                return "SPARK incorrectly connected to PDP";
            }
        }

        if(!simLED) {
            simLedBad();
        }

        if(getAllNull(5, 8)) {
            simLedBad();
            return "SPARK not connected to CAN chain";
        }

        if(!((getCAN(get(6), get(5)).equals("PDP") && getCAN(get(8), get(7)).equals("PCM")) ||
                (getCAN(get(6), get(5)).equals("PCM") && getCAN(get(8), get(7)).equals("PDP"))) &&
                !((getCAN(get(6), get(5)).equals("PDP") && getCAN(get(8), get(7)).equals("RoboRIO")) ||
                        (getCAN(get(6), get(5)).equals("RoboRIO") && getCAN(get(8), get(7)).equals("PDP")))) {
            String ans = "";

            if(!getCAN(get(6), get(5)).equals("PDP") && !getCAN(get(6), get(5)).equals("PCM") && !getCAN(get(6), get(5)).equals("RoboRIO")) {
                ans += getCAN(get(6), get(5));
            } else {
                if(getCAN(get(6), get(5)).equals("PDP")) {
                    ans += "CAN chain must reach PCM or RoboRIO";
                } else {
                    ans += "CAN chain must reach PDP";
                }
            }

            if(!getCAN(get(8), get(7)).equals("PDP") && !getCAN(get(8), get(7)).equals("PCM") && !getCAN(get(8), get(7)).equals("RoboRIO")) {
                if(!getCAN(get(8), get(7)).equals(ans)) {
                    if(ans.length() > 0) {
                        ans += "\n";
                    }
                    ans += getCAN(get(8), get(7));
                }
            } else {
                if(getCAN(get(8), get(7)).equals("PDP")) {
                    if (!ans.equals("CAN chain must reach PCM or RoboRIO")) {
                        if(ans.length() > 0) {
                            ans += "\n";
                        }
                        ans += "CAN chain must reach PCM or RoboRIO";
                    }
                } else {
                    if (!ans.equals("CAN chain must reach PDP")) {
                        if(ans.length() > 0) {
                            ans += "\n";
                        }
                        ans += "CAN chain must reach PDP";
                    }
                }
            }

            simLedBad();

            return ans;
        }

        if(!simLED || LEDs.get(0).getColor().equals("orange")) {
            simLED = true;
            LEDs.get(0).blink(20);
            LEDs.get(1).blink(20);
            LEDs.get(0).setColor("magenta");
            LEDs.get(0).setStatus("Valid signal");
            LEDs.get(1).setColor("magenta");
            LEDs.get(1).setStatus("Valid signal");
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
        if(!simLED || LEDs.get(0).getColor().equals("magenta")) {
            simLED = true;
            LEDs.get(0).blink(20);
            LEDs.get(1).blink(20);
            LEDs.get(0).setColor("orange");
            LEDs.get(0).setStatus("CAN chain error");
            LEDs.get(1).setColor("orange");
            LEDs.get(1).setStatus("CAN chain error");
        }
    }
}