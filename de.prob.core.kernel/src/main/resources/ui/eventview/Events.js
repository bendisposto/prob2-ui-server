Events = (function() {
    var extern = {}
    var session = Session()
    var sortMode = "normal"

    $(document).ready(function() {
        $('.dropdown-toggle').dropdown()

        $('.dropdown-menu input').click(function(e) {
            e.stopPropagation()
        })

        $("#numRand").keyup(function(e) {
            var isInt = /^([0-9]+)$/.exec(e.target.value)!=null

            if(!isInt && !$("#randomInput").hasClass('has-error')) {
                $("#randomInput").addClass('has-error')
                $("#randomX").prop("disabled",true)
            } else {
                $("#randomInput").removeClass('has-error')
                $("#randomX").prop("disabled",false)
            }
        });

        $("#random1").click(function(e) {random(1)})
        $("#random5").click(function(e) {random(5)})
        $("#random10").click(function(e) {random(10)})
        $("#randomX").click(function(e) {
            e.preventDefault();
            if(!$("#randomX").prop("disabled")) {
                random($("#numRand").val())
            }
        })

        $("#back").click(function(e) {
            e.preventDefault()
            session.sendCmd("back", {
                "client" : extern.client
            })
        })

        $("#forward").click(function(e) {
            e.preventDefault()
            session.sendCmd("forward", {
                "client" : extern.client
            })
        })

        $("#sort").click(function(e) {
            changeSortMode()
            session.sendCmd("sort", {
                "sortMode" : sortMode,
                "client" : extern.client
            })
        })

    })

    function changeSortMode() {
        if( sortMode === "normal" ) {
            sortMode = "aToZ"
        } else if( sortMode === "aToZ" ) {
            sortMode = "zToA"
        } else if( sortMode === "zToA" ) {
            sortMode = "normal"
        }
    }

    function setContent(ops_string) {
        var ops = JSON.parse(ops_string);
        var e = $("#events")
        e.children().remove()
        for (el in ops) {
            var v = ops[el]
            v.params = v.params.join(", ")
            e.append(session.render("/ui/eventview/operation.html", v))
        }
        $(".enabled").click(function(e) {
            var id = e.currentTarget.id
            id = id.substring(2,id.length)
            console.log(id)
            session.sendCmd("execute", {
                "id" : id,
                "client" : extern.client
            })
        })
    }

    function setBackEnabled(enabled) {
        $("#back").prop("disabled",!(enabled === "true"))
    }

    function setForwardEnabled(enabled) {
        $("#forward").prop("disabled",!(enabled === "true"))

    }

    function random(num) {
        session.sendCmd("random", {
            "num" : num,
            "client" : extern.client
        })
    }

    function setSortMode(mode) {
        this.sortMode = mode
    }

    extern.client = ""
    extern.init = session.init
    extern.setContent = function(data) {
        setContent(data.ops)
        setBackEnabled(data.canGoBack)
        setForwardEnabled(data.canGoForward)
        setSortMode(data.sortMode)
    }

    return extern;
}())