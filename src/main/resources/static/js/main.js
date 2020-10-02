
var customActions = {
    post: {method: 'POST', url: '/api/set_old_data'}
}
var customActions2 = {
    post: {method: 'POST', url: '/api/get_new_data'}
}

var getNewCargosApi = Vue.resource('/api/get_new_cargos');
var getAllCargosApi = Vue.resource('/api/get_cargos');
var setOldDataApi = Vue.resource('/api/set_old_data', {}, customActions);
var getNewDataApi = Vue.resource('/api/get_new_data', {}, customActions2);

var getOffApi = Vue.resource('/api/off');

var do_cycle = false;

Vue.component('message-form', {
    props: ['messages'],
    data: function () {
        return {
            text: '',
            work_mode: 'Старт',
            cyclicSearch: 'ВЫКЛЮЧЕН',
            watch_button_is_disabled: false,
            cur_time: '',
            back_color_button1: {'background-color': 'khaki'},
            back_color_button2: {'background-color': 'khaki'},
            back_color_button3: {'background-color': 'khaki'},
            back_color_button4: {'background-color': 'khaki'},
            back_color_button5: {'background-color': 'khaki'},
            back_color_button6: {'background-color': 'khaki'}
        }
    },
    template:
        '<div>' +
            '<div>' +
                '<input type="text" placeholder="Ссылка на страницу" v-model="text" size="100"' +
                '/>' +
            '</div>' +
            '<div> Циклический поиск новых заявок: {{cyclicSearch}}{{this.cur_time}} </div>' +
            '<div> Режим: {{work_mode}} </div>' +
            '<input type="button" value="Добавить к обработанным" v-on:click="post" ' +
                ':style="back_color_button1" ' +
                '@mouseover="background_color_over(1)" ' +
                '@mouseleave="background_color_leave(1)" ' +
            '/>' +
            '<input type="button" value="Отслеживать новые заявки" @click="watch" :disabled="watch_button_is_disabled" ' +
                ':style="back_color_button2" ' +
                '@mouseover="background_color_over(2)" ' +
                '@mouseleave="background_color_leave(2)" ' +
            '/>' +
            // '<input type="button" value="Остановить отслеживание" @click="stop_watch" ' +
            //     ':style="back_color_button3" ' +
            //     '@mouseover="background_color_over(3)" ' +
            //     '@mouseleave="background_color_leave(3)" ' +
            // '/>' +
            '<input type="button" value="Показать все заявки" @click="show_all" ' +
                ':style="back_color_button4" ' +
                '@mouseover="background_color_over(4)" ' +
                '@mouseleave="background_color_leave(4)" ' +
            '/>' +
            '<input type="button" value="Показать новые заявки" @click="show_new" ' +
                ':style="back_color_button5" ' +
                '@mouseover="background_color_over(5)" ' +
                '@mouseleave="background_color_leave(5)" ' +
            '/>' +
            '<input type="button" value="Выключить" @click="off" ' +
                ':style="back_color_button6" ' +
                '@mouseover="background_color_over(6)" ' +
                '@mouseleave="background_color_leave(6)" ' +
            '/>' +
        '</div>',
    methods:{
        background_color_over: function (i) {
            if(i == "1") {
                this.back_color_button1 = {'background-color': 'lightblue'};
            } else if(i == "2") {
                this.back_color_button2 = {'background-color': 'lightblue'};
            } else if(i == "3") {
                this.back_color_button3 = {'background-color': 'lightblue'};
            } else if(i == "4") {
                this.back_color_button4 = {'background-color': 'lightblue'};
            } else if(i == "5") {
                this.back_color_button5 = {'background-color': 'lightblue'};
            } else if(i == "6") {
                this.back_color_button6 = {'background-color': 'lightblue'};
            }
        },
        background_color_leave: function (i) {
            if(i == "1") {
                this.back_color_button1 = {'background-color': 'khaki'};
            } else if(i == "2") {
                this.back_color_button2 = {'background-color': 'khaki'};
            } else if(i == "3") {
                this.back_color_button3 = {'background-color': 'khaki'};
            } else if(i == "4") {
                this.back_color_button4 = {'background-color': 'khaki'};
            } else if(i == "5") {
                this.back_color_button5 = {'background-color': 'khaki'};
            } else if(i == "6") {
                this.back_color_button6 = {'background-color': 'khaki'};
            }
        },
        post: function () {
            this.messages.splice(0, this.messages.length);

            var message = { text: this.text};
            setOldDataApi.post({}, message).then( result =>
                result.json().then(content =>{
                    content.forEach(message => this.messages.push(message))
                })
            );

            if(this.work_mode != 'Циклический поиск новых заявок'){
                this.work_mode = 'Все заявки';
            }
        },
        watch: function () {
            if(this.cur_time == ''){
                var cd = new Date();
                this.cur_time = " с " + cd.getHours() + ":" + cd.getMinutes();
            }
            this.watch_button_is_disabled = true;
            this.cyclicSearch = 'ВКЛЮЧЕН';
            do_cycle = true
            this.do_cycle_in = true
            this.messages.splice(0, this.messages.length);
            var message = { text: this.text};
            getNewDataApi.post({}, message).then( result =>
                result.json().then(content =>{
                    content.forEach(message => this.messages.push(message))
                })
            );
            this.work_mode = 'Новые заявки';
            if(do_cycle == true) {
                setTimeout(this.watch, 61000);
            }
        },
        // stop_watch: function () {
        //     do_cycle = false;
        //     this.work_mode = 'Просмотр';
        //     this.do_cycle_in = false;
        // },
        show_all: function () {
            this.messages.splice(0, this.messages.length);
            getAllCargosApi.get().then(result =>
                result.json().then(content =>
                    content.forEach(cargo => this.messages.push(cargo))
                )
            );
            this.work_mode = 'Все заявки';
        },
        show_new: function() {
            this.messages.splice(0, this.messages.length);
            getNewCargosApi.get().then(result =>
                result.json().then(content =>
                    content.forEach(message => this.messages.push(message))
                )
            );
            this.work_mode = 'Новые заявки';
        },
        off: function() {
            getOffApi.get();
        }
    }
});

Vue.component('message-row', {
    props: ['message'],
    data: function () {
        return {
            back_color_table_row: {
                'background-color': 'white',
                'border': '1px solid black'
            }
        }
    },
    template:
        '<tr :style="back_color_table_row" @mouseover="background_color_over" @mouseleave="background_color_leave" >' +
            '<td style="border-bottom-style: inset; border-right-style: inset;">{{ message[0] }}</td>' +
            '<td style="border-bottom-style: inset; border-right-style: inset;">{{ message[1] }}</td>' +
            '<td style="border-bottom-style: inset; border-right-style: inset;">{{ message[2] }}</td>' +
            '<td style="border-bottom-style: inset; border-right-style: inset;">{{ message[3] }}</td>' +
            '<td style="border-bottom-style: inset; border-right-style: inset;">{{ message[4] }}</td>' +
            '<td style="border-bottom-style: inset; border-right-style: inset;">{{ message[5] }}</td>' +
            '<td style="border-bottom-style: inset; border-right-style: inset;">{{ message[6] }}</td>' +
            '<td style="border-bottom-style: inset; border-right-style: inset;">{{ message[7] }}</td>' +
            '<td style="border-bottom-style: inset; border-right-style: inset;"><a :href="message[8]">{{ message[8] }}</a></td>' +
            '<td style="border-bottom-style: inset; border-right-style: inset;">{{ message[9] }}</td>' +
            '<td style="border-bottom-style: inset"><a :href="message[10]">{{ message[10] }}</a></td>' +
        '</tr>',
    methods:{
        background_color_over: function () {
            this.back_color_table_row = {
                'background-color': 'lightblue',
                    'border': '3px solid black'
            };
        },
        background_color_leave: function () {
            this.back_color_table_row = {
                'background-color': 'white',
                'border': '1px solid black'
            };
        }
    }
});

Vue.component('messages-list111', {
    props: ['messages'],
    template:
        '<div>' +
            '<div>' +
                '<message-form :messages="messages" />' +
            '</div>' +
            '<table style="float:right" style="border: 1px solid black">' + //style="background-color:#00FF00"
                '<message-row v-for="message in messages" :message="message" />' + //:key="message[10]"
            '</table>' +
        '</div>',
    created: function() {
        getNewCargosApi.get().then(result =>
            result.json().then(content =>
                content.forEach(message => this.messages.push(message))
            )
        );

    }
});

var appData = {
    messages: []
}

var app = new Vue({
    el: '#app',
    template: '<messages-list111 :messages="messages"/>',
    data: appData
});