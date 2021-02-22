/**
 * @author Aimé
 * @email 
 * @create date 2021-01-26 22:17:55
 * @modify date 2021-01-26 22:17:55
 * @desc [description]
 */
package home;

import java.util.Map;

public interface IStateMap {
    public Map<String, Boolean> getState(); 
    public void speakersOn(); 
    public void speakersOff();
    public void lightsOn();
    public void lightsOff();
}
