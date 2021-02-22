/**
 * @author Aimé
 * @email 
 * @create date 2021-01-26 22:17:48
 * @modify date 2021-01-26 22:17:48
 * @desc [description]
 */
package home;

import java.util.Map;

public interface ISseResource {
    void broadcast(Map<String, Boolean> stateMap, String eventName);
}