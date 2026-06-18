import { createPage, ajax } from 'nc-lightapp-front';
import ListPage from './index.vue';

export default createPage({
    pageinit: function (props) {
        window.onbeforeunload = () => {
        };
    }
})(ListPage);
