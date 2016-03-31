package campaignmanager;

import java.util.List;

/**
 * Created by Anonym on 8. 3. 2016.
 */
public interface CampaignManager {

    /**
     *
     * @param hero
     * @param mission
     */
    void sendHeroToMission(Hero hero, Mission mission);

    /**
     *
     * @param hero
     * @param mission
     */
    void removeHeroFromMission(Hero hero, Mission mission);

    /**
     *
     * @param mission
     * @return
     */
    List<Hero> findHeroesByMission(Mission mission);

    /**
     *
     * @param hero
     * @return
     */
    Mission findMissionByHero(Hero hero);

}
