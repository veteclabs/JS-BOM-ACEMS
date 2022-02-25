<template>
    <div style="width:100%; height:100vh; position:absolute; top:0; left:0; background-color: rgba(0, 0, 0, 0.2);
z-index:9999;"
         v-show="propsdata.show">
        <div class="loading-box">
            <div class="loading-animation">
                <div class="lds-css ng-scope">
                    <div style="width:100%;height:100%" class="lds-flickr">
                        <div></div>
                        <div></div>
                        <div></div>
                    </div>
                </div>
            </div>
            <p>loading...</p>
        </div>
    </div>
</template>
<script>
export default {
  props: ['propsdata'],
};
</script>
<style scoped>
    .loading-box {
        width: 270px;
        height: 130px;
        border-radius: 10px;
        border: solid 1px rgba(255, 255, 255, 0.1);
        background-color: #fff;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        box-shadow: 0 10px 15px rgba(0, 0, 0, 0.2);
    }

    .loading-animation {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
    }

    .loading-box > p {
        position: absolute;
        top: 65%;
        left: 50%;
        transform: translate(-50%);
        letter-spacing: 1.5px;
        font-size: 12px;
        color: #99A1A0;
    }

    @keyframes lds-flickr-opacity {
        0% {
            -webkit-transform: translate(0 0);
            transform: translate(0 0);
            opacity: 1;
        }
        49.99% {
            opacity: 1;
            -webkit-transform: translate(80px, 0);
            transform: translate(80px, 0);
        }
        50% {
            opacity: 0;
            -webkit-transform: translate(80px, 0);
            transform: translate(80px, 0);
        }
        100% {
            opacity: 0;
            -webkit-transform: translate(0, 0);
            transform: translate(0, 0)
        }
    }

    @-webkit-keyframes lds-flickr-opacity {
        0% {
            -webkit-transform: translate(0 0);
            transform: translate(0 0);
            opacity: 1;
        }
        49.99% {
            opacity: 1;
            -webkit-transform: translate(80px, 0);
            transform: translate(80px, 0);
        }
        50% {
            opacity: 0;
            -webkit-transform: translate(80px, 0);
            transform: translate(80px, 0);
        }
        100% {
            opacity: 0;
            -webkit-transform: translate(0, 0);
            transform: translate(0, 0)
        }
    }

    @keyframes lds-flickr {
        0% {
            -webkit-transform: translate(0, 0);
            transform: translate(0, 0);
        }
        50% {
            -webkit-transform: translate(80px, 0);
            transform: translate(80px, 0);
        }
        100% {
            -webkit-transform: translate(0, 0);
            transform: translate(0, 0);
        }
    }

    @-webkit-keyframes lds-flickr {
        0% {
            -webkit-transform: translate(0, 0);
            transform: translate(0, 0);
        }
        50% {
            -webkit-transform: translate(80px, 0);
            transform: translate(80px, 0);
        }
        100% {
            -webkit-transform: translate(0, 0);
            transform: translate(0, 0);
        }
    }

    .lds-flickr {
        position: relative;
    }

    .lds-flickr div {
        position: absolute;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        top: 70px;
        left: 40px;
    }

    .lds-flickr div:nth-child(1) {
        background: #FFA100;
        animation: lds-flickr 1s linear infinite;
        -webkit-animation: lds-flickr 1s linear infinite;
        -webkit-animation-delay: -0.5s;
        animation-delay: -0.5s;
    }

    .lds-flickr div:nth-child(2) {
        background: #3363ff;
        -webkit-animation-delay: 0s;
        animation-delay: 0s;
        animation: lds-flickr 1s linear infinite;
        -webkit-animation: lds-flickr 1s linear infinite;
    }

    .lds-flickr div:nth-child(3) {
        background: #FFA100;
        -webkit-animation: lds-flickr-opacity 1s linear infinite;
        animation: lds-flickr-opacity 1s linear infinite;
        -webkit-animation-delay: -0.5s;
        animation-delay: -0.5s;
    }

    .lds-flickr {
        width: 200px !important;
        height: 200px !important;
        -webkit-transform: translate(-100px, -100px) scale(1) translate(100px, 100px);
        transform: translate(-100px, -100px) scale(1) translate(100px, 100px);
    }
</style>
