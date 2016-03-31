import campaignmanager.Hero;

/**
 * Created by Michaela Bamburová on 28.03.2016.
 */
public class HeroBuilder {


        private Long id;
        private String hero_name;
        private int hero_level;

        public HeroBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public HeroBuilder hero_name(String hero_name) {
            this.hero_name = hero_name;
            return this;
        }

        public HeroBuilder hero_level(int hero_level){
            this.hero_level = hero_level;
            return this;
        }

        public Hero build() {
            Hero hero = new Hero();
            hero.setId(id);
            hero.setName(hero_name);
            hero.setLevel(hero_level);
            return hero;
        }
}
