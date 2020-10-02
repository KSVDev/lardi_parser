package com.kozinets.sergey.lardi_parser;

public class Backup {
    /*




// var messageApi = Vue.resource('/api/get_cargos');
var getCargosApi = Vue.resource('/api/get_cargos');
var setOldDataApi = Vue.resource('/api/set_old_data');

Vue.component('message-form', {
    props: ['messages3'],
    data: function () {
        return {
            text: ''
        }
    },
    template:
        '<div>' +
            '<input type="text" placeholder="Ссылка на страницу" v-model="text" />' +
            '<input type="button" value="Взять текущие данные за основу" v-on:click="save" />' +
            '<input type="button" value="Отслеживать новые грузы" @click="watch" />' +
        '</div>',
    methods:{
        click: function () {
            var message = { text: this.text};

            setOldDataApi.save({}, message).then( result =>
                result.json().then(data =>{
                    this.messages3.push(data);
                })
            )
        }
    }
});

Vue.component('message-row', {
    props: ['message'],
    // template: '<div>{{ message[0] }} | {{ message[1] }} | {{ message[2] }} | {{ message[3] }} | {{ message[4] }} | {{ message[5] }}' +
    //     ' | {{ message[6] }} | {{ message[7] }} | {{ message[8] }} | {{ message[9] }} | {{ message[10] }} </div>'
    template: '<tr><td style="border: 1px solid black">{{ message[0] }}</td>' +
        '<td style="border: 1px solid black">{{ message[1] }}</td>' +
        '<td style="border: 1px solid black">{{ message[2] }}</td>' +
        '<td style="border: 1px solid black">{{ message[3] }}</td>' +
        '<td style="border: 1px solid black">{{ message[4] }}</td>' +
        '<td style="border: 1px solid black">{{ message[5] }}</td>' +
        '<td style="border: 1px solid black">{{ message[6] }}</td>' +
        '<td style="border: 1px solid black">{{ message[7] }}</td>' +
        '<td style="border: 1px solid black">{{ message[8] }}</td>' +
        '<td style="border: 1px solid black"><a :href="message[9]">{{ message[9] }}</a></td>' +
        '<td style="border: 1px solid black">{{ message[10] }}</td>'+
        '</tr>'
});

Vue.component('messages-list111', {
    props: ['messages1'],
    template:
        '<div>' +
        '<message-form :messages="messages" />' +
            '<table style="float:right" style="border: 1px solid black">' + //style="background-color:#00FF00"
                '<message-row v-for="message111 in messages1" :key="message111[10]" :message="message111" />' +
            '</table>' +
        '</div>',
    created: function() {
        getCargosApi.get().then(result =>
            // console.log(result)
            result.json().then(data =>
                data.forEach(message => this.messages1.push(message))
            )
        )
    }
});

var app = new Vue({
    el: '#app',
    template: '<messages-list111 :messages1="messages2"/>',
    data: {
        messages2: []
    }
});

     */
}
