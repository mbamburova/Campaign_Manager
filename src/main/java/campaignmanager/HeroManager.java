package campaignmanager;

import java.util.List;

/**
 * Created by Anonym on 8. 3. 2016.
 */
public interface HeroManager {

    /**
     *
     * @param hero
     */
    void createHero(Hero hero);

    /**
     *
     * @param hero
     */
    void updateHero(Hero hero);

    /**
     *
     * @param hero
     */
    void deleteHero(Hero hero);

    /**
     *
     * @param id
     * @return
     */
    Hero findHeroById(Long id);

    /**
     *
     * @return
     */
    List<Hero> findAllHeroes();


    List<Hero> viewFreeHeroes();
}
