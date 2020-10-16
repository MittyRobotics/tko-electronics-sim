package com.amhsrobotics.circuitsim.hardware.devices;

import com.amhsrobotics.circuitsim.hardware.Flippable;
import com.amhsrobotics.circuitsim.hardware.HardwareType;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;

public class Falcon extends Flippable {

    public Falcon() {}

    public Falcon(Vector2 position, boolean... addCrimped) {
        super(position, HardwareType.FALCON, addCrimped);


        for(JSONArray arr : pinDefs) {
            Sprite temp;
            if(connectors.size() == connNum) {
                break;
            }
            temp = new Sprite(new Texture(Gdx.files.internal("img/point.png")));
            temp.setCenter(position.x + (Long) arr.get(0), position.y + (Long) arr.get(1));
            temp.setSize((Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(0) /2f, (Long)pinSizeDefs.get(pinDefs.indexOf(arr)).get(1) /2f);
            connectors.add(temp);
        }

        initConnections();
        initEnds();
    }

    public Vector2 calculate(int port) {
        if(port == 0) {
            return calculateDirection(3+cur, port);
        } else if (port == 1) {
            return calculateDirection(1+cur, port);
        } else {
            return calculateDirection(cur, port);
        }
    }

    public String getCAN(Cable c1, Cable c2) {
        if(c1 == get(5) && c2 == get(4)) {
            if(getNull(3) || getNull(2)) {
                return "CAN chain ends at Falcon " + hardwareID2;
            } else if (getOther(2) instanceof PowerDistributionPanel && getOther(3) instanceof PowerDistributionPanel) {
                if((getNum(2) == 9 && getNum(3) == 8) || (getNum(2) == 7 && getNum(3) == 6)) {
                    return "PDP";
                } else {
                    return "Incorrectly wired to PDP";
                }
            } else if (getOther(2) instanceof PneumaticsControlModule && getOther(3) instanceof PneumaticsControlModule) {
                if((getNum(2) == 4 && getNum(3) == 5) || (getNum(2) == 3 && getNum(3) == 2)) {
                    return "PCM";
                }
                return "Incorrectly wired to PCM";
            } else if (!(((getOther(3) instanceof Talon && getOther(2) instanceof Talon) || (getOther(3) instanceof Spark && getOther(2) instanceof Spark) || (getOther(3) instanceof Falcon && getOther(2) instanceof Falcon)) && getONum(2) == getONum(3))) {
                return "CAN chain improperly wired at Falcon " + hardwareID2;
            } else {
                return getOther(3).getCAN(get(3), get(2));
            }
        } else if (c1 == get(3) && c2 == get(2)) {
            if(getNull(5) || getNull(4)) {
                return "CAN chain ends at Falcon " + hardwareID2;
            } else if (getOther(4) instanceof PowerDistributionPanel && getOther(5) instanceof PowerDistributionPanel) {
                if((getNum(4) == 9 && getNum(5) == 8) || (getNum(4) == 7 && getNum(5) == 6)) {
                    return "PDP";
                }
                return "Incorrectly wired to PDP";
            } else if (getOther(4) instanceof PneumaticsControlModule && getOther(5) instanceof PneumaticsControlModule) {
                if((getNum(4) == 4 && getNum(5) == 5) || (getNum(4) == 3 && getNum(5) == 2)) {
                    return "PCM";
                }
                return "Incorrectly wired to PCM";
            } else if (!(((getOther(5) instanceof Talon && getOther(4) instanceof Talon) || (getOther(5) instanceof Spark && getOther(4) instanceof Spark) || (getOther(5) instanceof Falcon && getOther(4) instanceof Falcon)) && getONum(4) == getONum(5))) {
                return "CAN chain improperly wired at Falcon " + hardwareID2;
            } else {
                return getOther(5).getCAN(get(5), get(4));
            }
        } else {
            return "CAN chain improperly wired at Falcon " + hardwareID2;
        }
    }

    public String check() {
        if(getNull(0) || getNull(1) || !(getOther(0) instanceof PowerDistributionPanel && getOther(1) instanceof PowerDistributionPanel)) {
            return "Falcon is not connected to PDP";
        }

        if(getNum(0) <= 41 && getNum(0) >= 34) {
            if(getNum(0) % 2 != 1 || getNum(1) != getNum(0) - 1) {
                return "Falcon incorrectly connected to PDP";
            }
        }

        if(getNum(0) <= 17 && getNum(0) >= 10) {
            if(getNum(0) % 2 != 1 || getNum(1) != getNum(0) - 1) {
                return "Falcon incorrectly connected to PDP";
            }
        }

        if(getNum(0) <= 33 && getNum(0) >= 26) {
            if(getNum(0) % 2 != 0 || getNum(1) != getNum(0) + 1) {
                return "Falcon incorrectly connected to PDP";
            }
        }

        if(getNum(0) <= 25 && getNum(0) >= 18) {
            if(getNum(0) % 2 != 0 || getNum(1) != getNum(0) + 1) {
                return "Falcon incorrectly connected to PDP";
            }
        }

        if(getAllNull(2, 5)) {
            return "Falcon not connected to CAN chain";
        }

        if(!((getCAN(get(3), get(2)).equals("PDP") && getCAN(get(5), get(4)).equals("PCM")) || (getCAN(get(3), get(2)).equals("PCM") && getCAN(get(5), get(4)).equals("PDP")))) {
            String ans = "";

            if(!getCAN(get(3), get(2)).equals("PDP") && !getCAN(get(3), get(2)).equals("PCM")) {
                ans += getCAN(get(3), get(2));
            } else {
                if(getCAN(get(3), get(2)).equals("PDP")) {
                    ans += "CAN chain must reach PCM";
                } else {
                    ans += "CAN chain must reach PDP";
                }
            }

            if(!getCAN(get(5), get(4)).equals("PDP") && !getCAN(get(5), get(4)).equals("PCM")) {
                if(!getCAN(get(5), get(4)).equals(ans)) {
                    if(ans.length() > 0) {
                        ans += "\n";
                    }
                    ans += getCAN(get(5), get(4));
                }
            } else {
                if(getCAN(get(5), get(4)).equals("PDP")) {
                    if (!ans.equals("CAN chain must reach PCM")) {
                        if(ans.length() > 0) {
                            ans += "\n";
                        }
                        ans += "CAN chain must reach PCM";
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

            return ans;
        }


        return null;
    }
}
