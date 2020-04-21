function Ce(a) {
    a = a.toLowerCase();
    for (var b = !0, c = "", d = 0; d < a.length; d++) {
        var e = a.charAt(d);
        /\.|\!|\?|\n|\r/.test(e) ? b = !0 : "" != e.trim() && 1 == b && (e = e.toUpperCase(), b = !1), c += e
    }
    return "/" == window.location.pathname && (c = c.replace(/\bi\b/g, "I")), c = _(c)
}

function Se(a) {
    a = a.toLowerCase();
    for (var b = "", c = 0; c < a.length; c++) {
        var d = a.charAt(c);
        b += c % 2 ? d.toUpperCase() : d
    }
    return b
}

function xe(a) {
    return c = a.toLowerCase(), c = (c + "").replace(/^(\S)|\s+(\S)/g, function(a) {
        return a.toUpperCase()
    }), c = _(c), c = c.replace(/\(([A-Za-z])/g, function(a) {
        return a.toUpperCase()
    }), c
}

function _(c) {
    return c = c.replace(/\"([A-Za-z])/g, function(a) {
        return a.toUpperCase()
    })
}

function Se(a) {
    a = a.toLowerCase();
    for (var b = "", c = 0; c < a.length; c++) {
        var d = a.charAt(c);
        b += c % 2 ? d.toUpperCase() : d
    }
    return b
}

function Be(a) {
    for (var s = "", i = 0; i < a.length; i++) {
        var n = a.charAt(i);
        s += n == n.toUpperCase() ? n.toLowerCase() : n.toUpperCase()
    }
    return s
}

function Ie(a) {
    return a = (a = (a = xe(a)).replace(/\b(A|An|And|As|At|But|By|En|For|If|In|Is|Of|On|Or|The|To|Vs?\\.?|Via)\b/g, function(_) {
        return _.toLowerCase()
    })).replace(/(?:([\.\?!] |\n|^))(a|an|and|as|at|but|by|en|for|if|in|is|of|on|or|the|to|vs?\\.?|via)/g, function(_) {
        return xe(_)
    })
}

function Ae(_, $, ee) {
    for (var te = "", c = 0; c < _.length; c++) {
        var ne = _.charAt(c),
            re = ne;
        if (ee) re = ne.toLowerCase();
        $[re] ? te += $[re] : te += ne
    }
    return te = te.replace(/\n/g, "<br>")
}

function Me(_, ee) {
    var $ = _.split("").reduce(function(_, $) {
        return _ + $ + ee
    }, "");
    return $ = $.replace(/\n/g, "<br>")
}

function je(_) {
    -1 < window.location.search.indexOf("hyphenate=true") && (_.value = _.value.replace(/ /g, "-"), _.placeholder = _.placeholder.replace(/ /g, "-")), -1 < window.location.search.indexOf("stripdashes=true") && (_.value = _.value.replace(/-/g, " "), _.placeholder = _.placeholder.replace(/-/g, " ")), -1 < window.location.search.indexOf("stripspaces=true") && (_.value = _.value.replace(/ /g, ""), _.placeholder = _.placeholder.replace(/ /g, ""))
}

function Oe(_, $) {
    "undefined" != typeof gtag && gtag("event", $, {
        event_category: _,
        value: parseInt(document.getElementById("word_count").innerHTML)
    })
}

function He(_) {
    document.getElementById("char_count").innerHTML = _.value.length;
    var $ = 0,
        ee = _.value.trim().replace(/\s+/gi, " ");
    0 < ee.length && ($ = ee.split(" ").length), document.getElementById("word_count").innerHTML = $;
    var te = 0;
    0 < _.value.length && (te = _.value.split(/\r*\n/).length), document.getElementById("line_count").innerHTML = te
}
var te;

function _e(_, $) {
    clearTimeout(te);
    var ee = document.querySelector(".messages");
    ee.innerHTML = '<div class="message ' + $ + '">' + _ + "</div>", te = setTimeout(function() {
        ee.innerHTML = ""
    }, 3e3)
}
var t, e, a, b, $ = Date.now();

function Re() {
    if ($ + 5e3 < Date.now()) {
        $ = Date.now()
    }
}

function De(_, ee) {
    return _.replace(/[\s\S]/g, function(_) {
        var $;
        return $ = _.charCodeAt().toString(2), _ = "00000000".slice(String($).length) + $, 0 == ee ? _ : _ + " "
    })
}

function qe(_) {
    var $ = (_ = (_ = _.replace(/\s+/g, "")).match(/.{1,8}/g).join(" ")).split(" "),
        ee = [];
    for (i = 0; i < $.length; i++) ee.push(String.fromCharCode(parseInt($[i], 2)));
    return ee.join("")
}
t = this, e = function() {
    return o = {}, r.m = n = [function(t, e) {
        t.exports = function(t) {
            var e;
            if ("SELECT" === t.nodeName) t.focus(), e = t.value;
            else if ("INPUT" === t.nodeName || "TEXTAREA" === t.nodeName) {
                var n = t.hasAttribute("readonly");
                n || t.setAttribute("readonly", ""), t.select(), t.setSelectionRange(0, t.value.length), n || t.removeAttribute("readonly"), e = t.value
            } else {
                t.hasAttribute("contenteditable") && t.focus();
                var o = window.getSelection(),
                    r = document.createRange();
                r.selectNodeContents(t), o.removeAllRanges(), o.addRange(r), e = o.toString()
            }
            return e
        }
    }, function(t, e) {
        function n() {}
        n.prototype = {
            on: function(t, e, n) {
                var o = this.e || (this.e = {});
                return (o[t] || (o[t] = [])).push({
                    fn: e,
                    ctx: n
                }), this
            },
            once: function(t, e, n) {
                var o = this;

                function r() {
                    o.off(t, r), e.apply(n, arguments)
                }
                return r._ = e, this.on(t, r, n)
            },
            emit: function(t) {
                for (var e = [].slice.call(arguments, 1), n = ((this.e || (this.e = {}))[t] || []).slice(), o = 0, r = n.length; o < r; o++) n[o].fn.apply(n[o].ctx, e);
                return this
            },
            off: function(t, e) {
                var n = this.e || (this.e = {}),
                    o = n[t],
                    r = [];
                if (o && e)
                    for (var i = 0, a = o.length; i < a; i++) o[i].fn !== e && o[i].fn._ !== e && r.push(o[i]);
                return r.length ? n[t] = r : delete n[t], this
            }
        }, t.exports = n, t.exports.TinyEmitter = n
    }, function(t, e, n) {
        var d = n(3),
            h = n(4);
        t.exports = function(t, e, n) {
            if (!t && !e && !n) throw new Error("Missing required arguments");
            if (!d.string(e)) throw new TypeError("Second argument must be a String");
            if (!d.fn(n)) throw new TypeError("Third argument must be a Function");
            if (d.node(t)) return s = e, f = n, (u = t).addEventListener(s, f), {
                destroy: function() {
                    u.removeEventListener(s, f)
                }
            };
            if (d.nodeList(t)) return a = t, c = e, l = n, Array.prototype.forEach.call(a, function(t) {
                t.addEventListener(c, l)
            }), {
                destroy: function() {
                    Array.prototype.forEach.call(a, function(t) {
                        t.removeEventListener(c, l)
                    })
                }
            };
            if (d.string(t)) return o = t, r = e, i = n, h(document.body, o, r, i);
            throw new TypeError("First argument must be a String, HTMLElement, HTMLCollection, or NodeList");
            var o, r, i, a, c, l, u, s, f
        }
    }, function(t, n) {
        n.node = function(t) {
            return void 0 !== t && t instanceof HTMLElement && 1 === t.nodeType
        }, n.nodeList = function(t) {
            var e = Object.prototype.toString.call(t);
            return void 0 !== t && ("[object NodeList]" === e || "[object HTMLCollection]" === e) && "length" in t && (0 === t.length || n.node(t[0]))
        }, n.string = function(t) {
            return "string" == typeof t || t instanceof String
        }, n.fn = function(t) {
            return "[object Function]" === Object.prototype.toString.call(t)
        }
    }, function(t, e, n) {
        var a = n(5);

        function i(t, e, n, o, r) {
            var i = function(e, n, t, o) {
                return function(t) {
                    t.delegateTarget = a(t.target, n), t.delegateTarget && o.call(e, t)
                }
            }.apply(this, arguments);
            return t.addEventListener(n, i, r), {
                destroy: function() {
                    t.removeEventListener(n, i, r)
                }
            }
        }
        t.exports = function(t, e, n, o, r) {
            return "function" == typeof t.addEventListener ? i.apply(null, arguments) : "function" == typeof n ? i.bind(null, document).apply(null, arguments) : ("string" == typeof t && (t = document.querySelectorAll(t)), Array.prototype.map.call(t, function(t) {
                return i(t, e, n, o, r)
            }))
        }
    }, function(t, e) {
        if ("undefined" != typeof Element && !Element.prototype.matches) {
            var n = Element.prototype;
            n.matches = n.matchesSelector || n.mozMatchesSelector || n.msMatchesSelector || n.oMatchesSelector || n.webkitMatchesSelector
        }
        t.exports = function(t, e) {
            for (; t && 9 !== t.nodeType;) {
                if ("function" == typeof t.matches && t.matches(e)) return t;
                t = t.parentNode
            }
        }
    }, function(t, e, n) {
        "use strict";
        n.r(e);
        var o = n(0),
            r = n.n(o),
            i = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function(t) {
                return typeof t
            } : function(t) {
                return t && "function" == typeof Symbol && t.constructor === Symbol && t !== Symbol.prototype ? "symbol" : typeof t
            };

        function a(t, e) {
            for (var n = 0; n < e.length; n++) {
                var o = e[n];
                o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(t, o.key, o)
            }
        }

        function c(t) {
            ! function(t, e) {
                if (!(t instanceof c)) throw new TypeError("Cannot call a class as a function")
            }(this), this.resolveOptions(t), this.initSelection()
        }
        var l = (function(t, e, n) {
                a(t.prototype, e)
            }(c, [{
                key: "resolveOptions",
                value: function(t) {
                    var e = 0 < arguments.length && void 0 !== t ? t : {};
                    this.action = e.action, this.container = e.container, this.emitter = e.emitter, this.target = e.target, this.text = e.text, this.trigger = e.trigger, this.selectedText = ""
                }
            }, {
                key: "initSelection",
                value: function() {
                    this.text ? this.selectFake() : this.target && this.selectTarget()
                }
            }, {
                key: "selectFake",
                value: function() {
                    var t = this,
                        e = "rtl" == document.documentElement.getAttribute("dir");
                    this.removeFake(), this.fakeHandlerCallback = function() {
                        return t.removeFake()
                    }, this.fakeHandler = this.container.addEventListener("click", this.fakeHandlerCallback) || !0, this.fakeElem = document.createElement("textarea"), this.fakeElem.style.fontSize = "12pt", this.fakeElem.style.border = "0", this.fakeElem.style.padding = "0", this.fakeElem.style.margin = "0", this.fakeElem.style.position = "absolute", this.fakeElem.style[e ? "right" : "left"] = "-9999px";
                    var n = window.pageYOffset || document.documentElement.scrollTop;
                    this.fakeElem.style.top = n + "px", this.fakeElem.setAttribute("readonly", ""), this.fakeElem.value = this.text, this.container.appendChild(this.fakeElem), this.selectedText = r()(this.fakeElem), this.copyText()
                }
            }, {
                key: "removeFake",
                value: function() {
                    this.fakeHandler && (this.container.removeEventListener("click", this.fakeHandlerCallback), this.fakeHandler = null, this.fakeHandlerCallback = null), this.fakeElem && (this.container.removeChild(this.fakeElem), this.fakeElem = null)
                }
            }, {
                key: "selectTarget",
                value: function() {
                    this.selectedText = r()(this.target), this.copyText()
                }
            }, {
                key: "copyText",
                value: function() {
                    var e = void 0;
                    try {
                        e = document.execCommand(this.action)
                    } catch (t) {
                        e = !1
                    }
                    this.handleResult(e)
                }
            }, {
                key: "handleResult",
                value: function(t) {
                    this.emitter.emit(t ? "success" : "error", {
                        action: this.action,
                        text: this.selectedText,
                        trigger: this.trigger,
                        clearSelection: this.clearSelection.bind(this)
                    })
                }
            }, {
                key: "clearSelection",
                value: function() {
                    this.trigger && this.trigger.focus(), document.activeElement.blur(), window.getSelection().removeAllRanges()
                }
            }, {
                key: "destroy",
                value: function() {
                    this.removeFake()
                }
            }, {
                key: "action",
                set: function(t) {
                    var e = 0 < arguments.length && void 0 !== t ? t : "copy";
                    if (this._action = e, "copy" !== this._action && "cut" !== this._action) throw new Error('Invalid "action" value, use either "copy" or "cut"')
                },
                get: function() {
                    return this._action
                }
            }, {
                key: "target",
                set: function(t) {
                    if (void 0 !== t) {
                        if (!t || "object" !== (void 0 === t ? "undefined" : i(t)) || 1 !== t.nodeType) throw new Error('Invalid "target" value, use a valid Element');
                        if ("copy" === this.action && t.hasAttribute("disabled")) throw new Error('Invalid "target" attribute. Please use "readonly" instead of "disabled" attribute');
                        if ("cut" === this.action && (t.hasAttribute("readonly") || t.hasAttribute("disabled"))) throw new Error('Invalid "target" attribute. You can\'t cut text from elements with "readonly" or "disabled" attributes');
                        this._target = t
                    }
                },
                get: function() {
                    return this._target
                }
            }]), c),
            u = n(1),
            s = n.n(u),
            f = n(2),
            d = n.n(f),
            h = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function(t) {
                return typeof t
            } : function(t) {
                return t && "function" == typeof Symbol && t.constructor === Symbol && t !== Symbol.prototype ? "symbol" : typeof t
            };

        function y(t, e) {
            for (var n = 0; n < e.length; n++) {
                var o = e[n];
                o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(t, o.key, o)
            }
        }
        var m = (function(t, e) {
            if ("function" != typeof e && null !== e) throw new TypeError("Super expression must either be null or a function, not " + typeof e);
            t.prototype = Object.create(e && e.prototype, {
                constructor: {
                    value: t,
                    enumerable: !1,
                    writable: !0,
                    configurable: !0
                }
            }), e && (Object.setPrototypeOf ? Object.setPrototypeOf(t, e) : t.__proto__ = e)
        }(v, s.a), function(t, e, n) {
            e && y(t.prototype, e), n && y(t, n)
        }(v, [{
            key: "resolveOptions",
            value: function(t) {
                var e = 0 < arguments.length && void 0 !== t ? t : {};
                this.action = "function" == typeof e.action ? e.action : this.defaultAction, this.target = "function" == typeof e.target ? e.target : this.defaultTarget, this.text = "function" == typeof e.text ? e.text : this.defaultText, this.container = "object" === h(e.container) ? e.container : document.body
            }
        }, {
            key: "listenClick",
            value: function(t) {
                var e = this;
                this.listener = d()(t, "click", function(t) {
                    return e.onClick(t)
                })
            }
        }, {
            key: "onClick",
            value: function(t) {
                var e = t.delegateTarget || t.currentTarget;
                this.clipboardAction && (this.clipboardAction = null), this.clipboardAction = new l({
                    action: this.action(e),
                    target: this.target(e),
                    text: this.text(e),
                    container: this.container,
                    trigger: e,
                    emitter: this
                })
            }
        }, {
            key: "defaultAction",
            value: function(t) {
                return b("action", t)
            }
        }, {
            key: "defaultTarget",
            value: function(t) {
                var e = b("target", t);
                if (e) return document.querySelector(e)
            }
        }, {
            key: "defaultText",
            value: function(t) {
                return b("text", t)
            }
        }, {
            key: "destroy",
            value: function() {
                this.listener.destroy(), this.clipboardAction && (this.clipboardAction.destroy(), this.clipboardAction = null)
            }
        }], [{
            key: "isSupported",
            value: function(t) {
                var e = 0 < arguments.length && void 0 !== t ? t : ["copy", "cut"],
                    n = "string" == typeof e ? [e] : e,
                    o = !!document.queryCommandSupported;
                return n.forEach(function(t) {
                    o = o && !!document.queryCommandSupported(t)
                }), o
            }
        }]), v);

        function v(t, e) {
            ! function(t, e) {
                if (!(t instanceof v)) throw new TypeError("Cannot call a class as a function")
            }(this);
            var n = function(t, e) {
                if (!t) throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
                return !e || "object" != typeof e && "function" != typeof e ? t : e
            }(this, (v.__proto__ || Object.getPrototypeOf(v)).call(this));
            return n.resolveOptions(e), n.listenClick(t), n
        }

        function b(t, e) {
            var n = "data-clipboard-" + t;
            if (e.hasAttribute(n)) return e.getAttribute(n)
        }
        e.default = m
    }], r.c = o, r.d = function(t, e, n) {
        r.o(t, e) || Object.defineProperty(t, e, {
            enumerable: !0,
            get: n
        })
    }, r.r = function(t) {
        "undefined" != typeof Symbol && Symbol.toStringTag && Object.defineProperty(t, Symbol.toStringTag, {
            value: "Module"
        }), Object.defineProperty(t, "__esModule", {
            value: !0
        })
    }, r.t = function(e, t) {
        if (1 & t && (e = r(e)), 8 & t) return e;
        if (4 & t && "object" == typeof e && e && e.__esModule) return e;
        var n = Object.create(null);
        if (r.r(n), Object.defineProperty(n, "default", {
                enumerable: !0,
                value: e
            }), 2 & t && "string" != typeof e)
            for (var o in e) r.d(n, o, function(t) {
                return e[t]
            }.bind(null, o));
        return n
    }, r.n = function(t) {
        var e = t && t.__esModule ? function() {
            return t.default
        } : function() {
            return t
        };
        return r.d(e, "a", e), e
    }, r.o = function(t, e) {
        return Object.prototype.hasOwnProperty.call(t, e)
    }, r.p = "", r(r.s = 6).default;

    function r(t) {
        if (o[t]) return o[t].exports;
        var e = o[t] = {
            i: t,
            l: !1,
            exports: {}
        };
        return n[t].call(e.exports, e, e.exports, r), e.l = !0, e.exports
    }
    var n, o
}, "object" == typeof exports && "object" == typeof module ? module.exports = e() : "function" == typeof define && define.amd ? define([], e) : "object" == typeof exports ? exports.ClipboardJS = e() : t.ClipboardJS = e(), a = this, b = function() {
    "use strict";

    function c(b, c, d) {
        var e = new XMLHttpRequest;
        e.open("GET", b), e.responseType = "blob", e.onload = function() {
            a(e.response, c, d)
        }, e.onerror = function() {
            console.error("could not download file")
        }, e.send()
    }

    function d(a) {
        var b = new XMLHttpRequest;
        b.open("HEAD", a, !1);
        try {
            b.send()
        } catch (a) {}
        return 200 <= b.status && b.status <= 299
    }

    function e(a) {
        try {
            a.dispatchEvent(new MouseEvent("click"))
        } catch (c) {
            var b = document.createEvent("MouseEvents");
            b.initMouseEvent("click", !0, !0, window, 0, 0, 0, 80, 20, !1, !1, !1, !1, 0, null), a.dispatchEvent(b)
        }
    }
    var f = "object" == typeof window && window.window === window ? window : "object" == typeof self && self.self === self ? self : "object" == typeof global && global.global === global ? global : void 0,
        a = f.saveAs || ("object" != typeof window || window !== f ? function() {} : "download" in HTMLAnchorElement.prototype ? function(b, g, h) {
            var i = f.URL || f.webkitURL,
                j = document.createElement("a");
            g = g || b.name || "download", j.download = g, j.rel = "noopener", "string" == typeof b ? (j.href = b, j.origin === location.origin ? e(j) : d(j.href) ? c(b, g, h) : e(j, j.target = "_blank")) : (j.href = i.createObjectURL(b), setTimeout(function() {
                i.revokeObjectURL(j.href)
            }, 4e4), setTimeout(function() {
                e(j)
            }, 0))
        } : "msSaveOrOpenBlob" in navigator ? function(f, g, h) {
            if (g = g || f.name || "download", "string" != typeof f) navigator.msSaveOrOpenBlob(function(a, b) {
                return void 0 === b ? b = {
                    autoBom: !1
                } : "object" != typeof b && (console.warn("Deprecated: Expected third argument to be a object"), b = {
                    autoBom: !b
                }), b.autoBom && /^\s*(?:text\/\S*|application\/xml|\S*\/\S*\+xml)\s*;.*charset\s*=\s*utf-8/i.test(a.type) ? new Blob(["\ufeff", a], {
                    type: a.type
                }) : a
            }(f, h), g);
            else if (d(f)) c(f, g, h);
            else {
                var i = document.createElement("a");
                i.href = f, i.target = "_blank", setTimeout(function() {
                    e(i)
                })
            }
        } : function(a, b, d, e) {
            if ((e = e || open("", "_blank")) && (e.document.title = e.document.body.innerText = "downloading..."), "string" == typeof a) return c(a, b, d);
            var g = "application/octet-stream" === a.type,
                h = /constructor/i.test(f.HTMLElement) || f.safari,
                i = /CriOS\/[\d]+/.test(navigator.userAgent);
            if ((i || g && h) && "object" == typeof FileReader) {
                var j = new FileReader;
                j.onloadend = function() {
                    var a = j.result;
                    a = i ? a : a.replace(/^data:[^;]*;/, "data:attachment/file;"), e ? e.location.href = a : location = a, e = null
                }, j.readAsDataURL(a)
            } else {
                var k = f.URL || f.webkitURL,
                    l = k.createObjectURL(a);
                e ? e.location = l : location.href = l, e = null, setTimeout(function() {
                    k.revokeObjectURL(l)
                }, 4e4)
            }
        });
    f.saveAs = a.saveAs = a, "undefined" != typeof module && (module.exports = a)
}, "function" == typeof define && define.amd ? define([], b) : "undefined" != typeof exports ? b() : (b(), a.FileSaver = {}), document.addEventListener("DOMContentLoaded", function() {
    var ne = document.getElementById("content");
    if (null != ne) {
        if (document.getElementById("upper") && document.getElementById("upper").addEventListener("click", function(e) {
                return e.preventDefault(), ne.value = ne.value.toUpperCase(), ne.placeholder = ne.placeholder.toUpperCase(), je(ne), Oe("Convert", "Upper"), Re(), !1
            }), document.getElementById("lower") && document.getElementById("lower").addEventListener("click", function(e) {
                return e.preventDefault(), ne.value = ne.value.toLowerCase(), ne.placeholder = ne.placeholder.toLowerCase(), je(ne), Oe("Convert", "Lower"), Re(), !1
            }), document.getElementById("capitalized") && document.getElementById("capitalized").addEventListener("click", function(e) {
                return e.preventDefault(), ne.value = xe(ne.value.toLowerCase()), ne.placeholder = xe(ne.placeholder.toLowerCase()), je(ne), Oe("Convert", "Capitalized"), Re(), !1
            }), document.getElementById("sentence") && document.getElementById("sentence").addEventListener("click", function(e) {
                return e.preventDefault(), ne.value = Ce(ne.value), ne.placeholder = Ce(ne.placeholder), je(ne), Oe("Convert", "Sentence"), Re(), !1
            }), document.getElementById("alternating") && document.getElementById("alternating").addEventListener("click", function(e) {
                return e.preventDefault(), ne.value = Se(ne.value), ne.placeholder = Se(ne.placeholder), je(ne), Oe("Convert", "Alternating"), Re(), !1
            }), document.getElementById("inverse") && document.getElementById("inverse").addEventListener("click", function(e) {
                return e.preventDefault(), ne.value = Be(ne.value), ne.placeholder = Be(ne.placeholder), je(ne), Oe("Convert", "Inverse"), Re(), !1
            }), document.getElementById("clear") && document.getElementById("clear").addEventListener("click", function(e) {
                return e.preventDefault(), ne.value = "", ne.placeholder = Ce(ne.placeholder.toLowerCase()), He(ne), Oe("Clear", "Clear"), Re(), !1
            }), document.getElementById("smalltext")) {
            var $ = {
                    0: "⁰",
                    1: "¹",
                    2: "²",
                    3: "³",
                    4: "⁴",
                    5: "⁵",
                    6: "⁶",
                    7: "⁷",
                    8: "⁸",
                    9: "⁹",
                    "+": "⁺",
                    "-": "⁻",
                    "=": "⁼",
                    "(": "⁽",
                    ")": "⁾",
                    a: "ᵃ",
                    b: "ᵇ",
                    c: "ᶜ",
                    d: "ᵈ",
                    e: "ᵉ",
                    f: "ᶠ",
                    g: "ᵍ",
                    h: "ʰ",
                    i: "ⁱ",
                    j: "ʲ",
                    k: "ᵏ",
                    l: "ˡ",
                    m: "ᵐ",
                    n: "ⁿ",
                    o: "ᵒ",
                    p: "ᵖ",
                    q: "ᵠ",
                    r: "ʳ",
                    s: "ˢ",
                    t: "ᵗ",
                    u: "ᵘ",
                    v: "ᵛ",
                    w: "ʷ",
                    x: "ˣ",
                    y: "ʸ",
                    z: "ᶻ"
                },
                ee = {
                    a: "ᴀ",
                    b: "ʙ",
                    c: "ᴄ",
                    d: "ᴅ",
                    e: "ᴇ",
                    f: "ꜰ",
                    g: "ɢ",
                    h: "ʜ",
                    i: "ɪ",
                    j: "ᴊ",
                    k: "ᴋ",
                    l: "ʟ",
                    m: "ᴍ",
                    n: "ɴ",
                    o: "ᴏ",
                    p: "ᴘ",
                    r: "ʀ",
                    s: "ꜱ",
                    t: "ᴛ",
                    u: "ᴜ",
                    v: "ᴠ",
                    w: "ᴡ",
                    y: "ʏ",
                    z: "ᴢ"
                };

            function _() {
                var _ = ne.value;
                "" == _ && (_ = ne.placeholder), document.getElementById("output").innerHTML = "<h3>" + smallcaps + "</h3><p>" + Ae(_, ee, !0) + "</p>", document.getElementById("output").innerHTML += "<h3>" + superscript + "</h3><p>" + Ae(_, $, !0) + "</p>"
            }
            _(), ne.addEventListener("input", function(e) {
                _(), Re()
            }), ne.addEventListener("propertychange", function(e) {
                _(), Re()
            })
        }
        if (document.getElementById("widetext")) {
            var te = {
                " ": " ",
                0: "０",
                1: "１",
                2: "２",
                3: "３",
                4: "４",
                5: "５",
                6: "６",
                7: "７",
                8: "８",
                9: "９",
                a: "ａ",
                b: "ｂ",
                c: "ｃ",
                d: "ｄ",
                e: "ｅ",
                f: "ｆ",
                g: "ｇ",
                h: "ｈ",
                i: "ｉ",
                j: "ｊ",
                k: "ｋ",
                l: "ｌ",
                m: "ｍ",
                n: "ｎ",
                o: "ｏ",
                p: "ｐ",
                q: "ｑ",
                r: "ｒ",
                s: "ｓ",
                t: "ｔ",
                u: "ｕ",
                v: "ｖ",
                w: "ｗ",
                x: "ｘ",
                y: "ｙ",
                z: "ｚ",
                A: "Ａ",
                B: "Ｂ",
                C: "Ｃ",
                D: "Ｄ",
                E: "Ｅ",
                F: "Ｆ",
                G: "Ｇ",
                H: "Ｈ",
                I: "Ｉ",
                J: "Ｊ",
                K: "Ｋ",
                L: "Ｌ",
                M: "Ｍ",
                N: "Ｎ",
                O: "Ｏ",
                P: "Ｐ",
                Q: "Ｑ",
                R: "Ｒ",
                S: "Ｓ",
                T: "Ｔ",
                U: "Ｕ",
                V: "Ｖ",
                W: "Ｗ",
                X: "Ｘ",
                Y: "Ｙ",
                Z: "Ｚ",
                "!": "！",
                '"': "゛",
                "#": "＃",
                $: "＄",
                "%": "％",
                "&": "＆",
                "(": "（",
                ")": "）",
                "*": "＊",
                "+": "＋",
                ",": "、",
                "-": "ー",
                ".": "。",
                "/": "／",
                ":": "：",
                ";": "；",
                "<": "〈",
                "=": "＝",
                ">": "〉",
                "?": "？",
                "@": "＠",
                "[": "［",
                "'": "'",
                "]": "］",
                "^": "＾",
                _: "＿",
                "`": "‘",
                "{": "｛",
                "|": "｜",
                "}": "｝",
                "~": "～"
            };

            function re() {
                var _ = ne.value;
                "" == _ && (_ = ne.placeholder), document.getElementById("output").innerHTML = "<p>" + Ae(_, te, !1) + "</p>"
            }
            re(), ne.addEventListener("input", function(e) {
                re(), Re()
            }), ne.addEventListener("propertychange", function(e) {
                re(), Re()
            })
        }
        if (document.getElementById("boldtext")) {
            var oe = {
                A: "𝗔",
                B: "𝗕",
                C: "𝗖",
                D: "𝗗",
                E: "𝗘",
                F: "𝗙",
                G: "𝗚",
                H: "𝗛",
                I: "𝗜",
                J: "𝗝",
                K: "𝗞",
                L: "𝗟",
                M: "𝗠",
                N: "𝗡",
                O: "𝗢",
                P: "𝗣",
                Q: "𝗤",
                R: "𝗥",
                S: "𝗦",
                T: "𝗧",
                U: "𝗨",
                V: "𝗩",
                W: "𝗪",
                X: "𝗫",
                Y: "𝗬",
                Z: "𝗭",
                a: "𝗮",
                b: "𝗯",
                c: "𝗰",
                d: "𝗱",
                e: "𝗲",
                f: "𝗳",
                g: "𝗴",
                h: "𝗵",
                i: "𝗶",
                j: "𝗷",
                k: "𝗸",
                l: "𝗹",
                m: "𝗺",
                n: "𝗻",
                o: "𝗼",
                p: "𝗽",
                q: "𝗾",
                r: "𝗿",
                s: "𝘀",
                t: "𝘁",
                u: "𝘂",
                v: "𝘃",
                w: "𝘄",
                x: "𝘅",
                y: "𝘆",
                z: "𝘇",
                0: "𝟬",
                1: "𝟭",
                2: "𝟮",
                3: "𝟯",
                4: "𝟰",
                5: "𝟱",
                6: "𝟲",
                7: "𝟳",
                8: "𝟴",
                9: "𝟵"
            };

            function ie() {
                var _ = ne.value;
                "" == _ && (_ = ne.placeholder), document.getElementById("output").innerHTML = "<p>" + Ae(_, oe, !1) + "</p>"
            }
            ie(), ne.addEventListener("input", function(e) {
                ie(), Re()
            }), ne.addEventListener("propertychange", function(e) {
                ie(), Re()
            })
        }
        if (document.getElementById("italictext")) {
            var ae = {
                A: "𝘈",
                B: "𝘉",
                C: "𝘊",
                D: "𝘋",
                E: "𝘌",
                F: "𝘍",
                G: "𝘎",
                H: "𝘏",
                I: "𝘐",
                J: "𝘑",
                K: "𝘒",
                L: "𝘓",
                M: "𝘔",
                N: "𝘕",
                O: "𝘖",
                P: "𝘗",
                Q: "𝘘",
                R: "𝘙",
                S: "𝘚",
                T: "𝘛",
                U: "𝘜",
                V: "𝘝",
                W: "𝘞",
                X: "𝘟",
                Y: "𝘠",
                Z: "𝘡",
                a: "𝘢",
                b: "𝘣",
                c: "𝘤",
                d: "𝘥",
                e: "𝘦",
                f: "𝘧",
                g: "𝘨",
                h: "𝘩",
                i: "𝘪",
                j: "𝘫",
                k: "𝘬",
                l: "𝘭",
                m: "𝘮",
                n: "𝘯",
                o: "𝘰",
                p: "𝘱",
                q: "𝘲",
                r: "𝘳",
                s: "𝘴",
                t: "𝘵",
                u: "𝘶",
                v: "𝘷",
                w: "𝘸",
                x: "𝘹",
                y: "𝘺",
                z: "𝘻"
            };

            function le() {
                var _ = ne.value;
                "" == _ && (_ = ne.placeholder), document.getElementById("output").innerHTML = "<p>" + Ae(_, ae, !1) + "</p>"
            }
            le(), ne.addEventListener("input", function(e) {
                le(), Re()
            }), ne.addEventListener("propertychange", function(e) {
                le(), Re()
            })
        }
        if (document.getElementById("underlinetext")) {
            function ce() {
                var _ = ne.value;
                "" == _ && (_ = ne.placeholder), document.getElementById("output").innerHTML = "<p>" + Me(_, "̲") + "</p>"
            }
            ce(), ne.addEventListener("input", function(e) {
                ce(), Re()
            }), ne.addEventListener("propertychange", function(e) {
                ce(), Re()
            })
        }
        if (document.getElementById("upsidedown")) {
            var ue = {
                " ": " ",
                a: "ɐ",
                b: "q",
                c: "ɔ",
                d: "p",
                e: "ǝ",
                f: "ɟ",
                g: "ƃ",
                h: "ɥ",
                i: "ᴉ",
                j: "ɾ",
                k: "ʞ",
                l: "l",
                m: "ɯ",
                n: "u",
                o: "o",
                p: "d",
                q: "b",
                r: "ɹ",
                s: "s",
                t: "ʇ",
                u: "n",
                v: "ʌ",
                w: "ʍ",
                x: "x",
                y: "ʎ",
                z: "z",
                A: "∀",
                B: "B",
                C: "Ɔ",
                D: "D",
                E: "Ǝ",
                F: "Ⅎ",
                G: "פ",
                H: "H",
                I: "I",
                J: "ſ",
                K: "K",
                L: "˥",
                M: "W",
                N: "N",
                O: "O",
                P: "Ԁ",
                Q: "Q",
                R: "R",
                S: "S",
                T: "┴",
                U: "∩",
                V: "Λ",
                W: "M",
                X: "X",
                Y: "⅄",
                Z: "Z",
                0: "0",
                1: "Ɩ",
                2: "ᄅ",
                3: "Ɛ",
                4: "ㄣ",
                5: "ϛ",
                6: "9",
                7: "ㄥ",
                8: "8",
                9: "6",
                ",": "'",
                ".": "˙",
                "?": "¿",
                "!": "¡",
                '"': ",,",
                "'": ",",
                "`": ",",
                "(": ")",
                ")": "(",
                "[": "]",
                "]": "[",
                "{": "}",
                "}": "{",
                "<": ">",
                ">": "<",
                "&": "⅋",
                _: "‾"
            };

            function se() {
                var _ = ne.value;
                "" == _ && (_ = ne.placeholder), _ = _.split("").reverse().join(""), document.getElementById("output").innerHTML = "<p>" + Ae(_, ue, !1) + "</p>"
            }
            se(), ne.addEventListener("input", function(e) {
                se(), Re()
            }), ne.addEventListener("propertychange", function(e) {
                se(), Re()
            })
        }
        if (document.getElementById("strikethrough")) {
            function de() {
                var _ = ne.value;
                "" == _ && (_ = ne.placeholder), document.getElementById("output").innerHTML = "<p>" + Me(_, "̶") + "</p>"
            }
            de(), ne.addEventListener("input", function(e) {
                de(), Re()
            }), ne.addEventListener("propertychange", function(e) {
                de(), Re()
            })
        }
        if (document.getElementById("backwards")) {
            function fe() {
                var _ = ne.value;
                "" == _ && (_ = ne.placeholder), document.getElementById("output").innerHTML = "<p>" + _.split("").reverse().join("").replace(/\n/g, "<br>") + "</p>"
            }
            fe(), ne.addEventListener("input", function(e) {
                fe(), Re()
            }), ne.addEventListener("propertychange", function(e) {
                fe(), Re()
            })
        }
        if (document.getElementById("morse")) {
            var pe = document.getElementById("translated");

            function ve(_) {
                var $ = ne,
                    ee = "translated";
                if (_) $ = pe, ee = "content";
                var te = $.value;
                "" == te && (te = $.placeholder), document.getElementById(ee).value = function(_, $) {
                    var ee = {
                        a: ".-",
                        b: "-...",
                        c: "-.-.",
                        d: "-..",
                        e: ".",
                        f: "..-.",
                        g: "--.",
                        h: "....",
                        i: "..",
                        j: ".---",
                        k: "-.-",
                        l: ".-..",
                        m: "--",
                        n: "-.",
                        o: "---",
                        p: ".--.",
                        q: "--.-",
                        r: ".-.",
                        s: "...",
                        t: "-",
                        u: "..-",
                        v: "...-",
                        w: ".--",
                        x: "-..-",
                        y: "-.--",
                        z: "--..",
                        1: ".----",
                        2: "..---",
                        3: "...--",
                        4: "....-",
                        5: ".....",
                        6: "-....",
                        7: "--...",
                        8: "---..",
                        9: "----.",
                        0: "-----",
                        ".": ".-.-.-",
                        ",": "--..--",
                        "?": "..--..",
                        "'": ".----.",
                        "/": "-..-.",
                        "(": "-.--.",
                        ")": "-.--.-",
                        "&": ".-...",
                        ":": "---...",
                        ";": "-.-.-.",
                        "=": "-...-",
                        "+": ".-.-.",
                        "-": "-....-",
                        _: "..--.-",
                        '"': ".-..-.",
                        $: "...-..-",
                        "!": "-.-.--",
                        "@": ".--.-.",
                        " ": "/"
                    };
                    if ($) {
                        var k, te = {};
                        for (k in ee) ee.hasOwnProperty(k) && (te[ee[k]] = k);
                        return ee = te, Ce((_ = (_ = _.replace(/\_/g, "-")).replace(/\|/g, "/")).split(" ").filter(function(v) {
                            return ee.hasOwnProperty(v.toLowerCase())
                        }).map(function(v) {
                            return ee[v.toLowerCase()]
                        }).join(""))
                    }
                    return _.split("").filter(function(v) {
                        return ee.hasOwnProperty(v.toLowerCase())
                    }).map(function(v) {
                        return ee[v.toLowerCase()].toUpperCase()
                    }).join(" ").replace(/,\/,/g, "/")
                }(te, _), _ && He(ne)
            }
            ve(!1), ne.addEventListener("input", function(e) {
                ve(!1), Re()
            }), ne.addEventListener("propertychange", function(e) {
                ve(!1), Re()
            }), pe.addEventListener("input", function(e) {
                ve(!0), Re()
            }), pe.addEventListener("propertychange", function(e) {
                ve(!0), Re()
            })
        }
        if (document.getElementById("binary")) {
            pe = document.getElementById("translated");

            function he(_) {
                var $ = ne,
                    ee = "translated";
                if (_) $ = pe, ee = "content";
                var te = $.value;
                "" == te && (te = $.placeholder), _ ? (document.getElementById(ee).value = qe(te), He(ne)) : document.getElementById(ee).value = De(te)
            }
            he(!1), ne.addEventListener("input", function(e) {
                he(!1), Re()
            }), ne.addEventListener("propertychange", function(e) {
                he(!1), Re()
            }), pe.addEventListener("input", function(e) {
                he(!0), Re()
            }), pe.addEventListener("propertychange", function(e) {
                he(!0), Re()
            })
        }
        ne.addEventListener("focus", function() {
            He(ne)
        }), ne.addEventListener("blur", function() {
            He(ne)
        }), ne.addEventListener("input", function() {
            He(ne)
        }), ne.addEventListener("propertychange", function() {
            He(ne)
        }), He(ne);
        var ye = new ClipboardJS("#copy");
        ye.on("success", function(e) {
            _e(copied, "success"), Oe("Copied", "Copied"), Re(), e.clearSelection()
        }), ye.on("error", function(e) {
            _e(manual_copy, "info"), Oe("Copied", "Manual"), Re()
        });
        try {
            new Blob;
            var ge = document.getElementById("download");
            ge && (ge.style.display = "inline-block", ge.addEventListener("click", function(e) {
                if (0 == ne.length) _e(no_text, "error");
                else {
                    if ("innerText" === ge.getAttribute("data-download-type")) var _ = document.getElementById(ge.getAttribute("data-download-target").slice(1)).innerText.replace(/\n/g, "\r\n");
                    else _ = document.getElementById(ge.getAttribute("data-download-target").slice(1)).value.replace(/\n/g, "\r\n");
                    var $ = new Blob([_], {
                        type: "text/plain;charset=utf-8"
                    });
                    saveAs($, file_name), _e(downloaded, "success"), Oe("Download", "Download"), Re()
                }
                return !1
            }))
        } catch (e) {}
        for (var Ee = document.querySelectorAll(".share"), i = 0; i < Ee.length; i++) Ee[i].addEventListener("click", function(e) {
            var _, $, ee, te, ne;
            return e.preventDefault(), _ = this.href, $ = 520, ee = 320, te = screen.width / 2 - $ / 2, ne = screen.height / 2 - ee / 2, window.open(_, "", "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,width=" + $ + ",height=" + ee + ",top=" + ne + ",left=" + te), !1
        })
    }
    var be = "ACCEPTCONSENT";
    if ("y" != function(_) {
            for (var $ = _ + "=", ee = document.cookie.split(";"), i = 0; i < ee.length; i++) {
                for (var c = ee[i];
                    " " == c.charAt(0);) c = c.substring(1);
                if (0 == c.indexOf($)) return c.substring($.length, c.length)
            }
            return ""
        }(be)) {
    }
    var ke = window.location.pathname.split("/")[1];
})