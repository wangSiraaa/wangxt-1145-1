import { createPage, ajax } from 'nc-lightapp-front';
import CardPage from './index.vue';

export default createPage({
    pageinit: function (props) {
        window.onbeforeunload = () => {
        };
    }
})(CardPage);
