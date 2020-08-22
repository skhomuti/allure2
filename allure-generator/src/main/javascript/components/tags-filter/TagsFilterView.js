import './styles.scss';
import 'selectize.js';
import {className, on} from '../../decorators';
import $ from 'jquery';
import template from './TagsFilterView.hbs';
import {View} from 'backbone.marionette';
import {Model} from 'backbone';

export const TAGS_QUERY_KEY = 'tagsQuery';

@className('tags')
class TagsFilterView extends View {
    template = template;


    initialize({state}) {
        this.state = state;
        this.model = new TagsModel();
        this.listenTo(this.model, 'sync', this.render);
        this.model.fetch();
//        this.$('.tags__input').on('change', null, this, ev => { ev.data.state.set(TAGS_QUERY_KEY, ev.value) })
    }


//    onBeforeRender() {
//        console.log('before')
//    }

    onRender() {
        this.$('.tags__input').selectize({
                create: true,
                sortField: 'text'
            })

//        this.$('.tags__input').selectivity({
//                items: this.model.get('tags'),
//                multiple: true,
//                placeholder: 'Type to search a tags'
//            })
    }


    close() {
        this.state.set(TAGS_QUERY_KEY, '');
    }
}

class TagsModel extends Model {

    url() {
        return 'data/tags.json'
    }
}
export default TagsFilterView;