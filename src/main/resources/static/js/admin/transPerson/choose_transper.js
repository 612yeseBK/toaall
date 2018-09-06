//这个js版本不支持箭头函数，所以无法获取vue的实例，我只能退而求其次，曲线救国
new Vue({
    el: '#chostrans',
    data: {
        message:'helloworld',
        forms: [{id:'3',name:'一号申请',description:'这是一号申请'},{id:'3',name:'2号申请',description:'这是2号申请'},{id:'3',name:'3号申请',description:'这是3号申请'}]
    },
    mounted: function () {
        this.get_forms(this)
    },
    methods: {
        get_forms: function (me) {
            AjaxTool.post('transper/getFormsInfo',{}, function (data) {
                    console.log(data)
                    me.forms = data
                }
            )
        },
        chooseWf:function (me,id) {
            AjaxTool.html('transper/getOneFormHtml',{id: id},function (html) {
                $('.portlet-body').html(html);
            });
        },
        chooseWf_hock: function (id) {
            this.chooseWf(this,id)
        }
    },
    watch: {
        message: function (msg) {
            // alert(msg)
            console.log(msg)
        }
    }
})