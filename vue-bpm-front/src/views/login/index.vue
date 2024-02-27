<template>
  <div class="login-page">
    <div class="page-left">
      <img src="/image/login/login-logo-fox.png" class="logo" />
      <login-box />
    </div>
    <!--    <div class="page-right">-->
    <!--      <el-carousel height="100%" :autoplay="true">-->
    <!--        <el-carousel-item v-for="item in carouselList" :key="item.key">-->
    <!--          <div class="carousel-item">-->
    <!--            <img :src="item.bg" class="item-bg" />-->
    <!--            <div class="item-content">-->
    <!--              <img :src="item.pic" class="item-pic" />-->
    <!--              <div class="item-text">-->
    <!--                <img :src="item.text1" />-->
    <!--              </div>-->
    <!--              <div class="item-text">-->
    <!--                <img :src="item.text2" />-->
    <!--              </div>-->
    <!--            </div>-->
    <!--          </div>-->
    <!--        </el-carousel-item>-->
    <!--      </el-carousel>-->
    <!--    </div>-->
  </div>
</template>
<script>
  import loginBox from './login.vue';
  export default {
    components: {
      loginBox
    },
    data() {
      return {
        carouselList: [
          {
            key: '1',
            bg: '/image/login/login1-bg.png',
            pic: '/image/login/login1-pic.png',
            text1: '/image/login/login1-text1.png',
            text2: '/image/login/login1-text2.png'
          },
          {
            key: '2',
            bg: '/image/login/login2-bg.png',
            pic: '/image/login/login2-pic.png',
            text1: '/image/login/login2-text1.png',
            text2: '/image/login/login2-text2.png'
          },
          {
            key: '3',
            bg: '/image/login/login3-bg.png',
            pic: '/image/login/login3-pic.png',
            text1: '/image/login/login3-text1.png',
            text2: '/image/login/login3-text2.png'
          }
        ]
      };
    }
  };
</script>

<style lang="scss">
  .login-page {
    display: flex;
    width: 100%;
    height: 100%;
    .page-left {
      position: relative;
      flex: 1;
      background: #235fd6 url('/image/login/loginBg.png') center center no-repeat;
      background-size: contain;
      .logo {
        position: absolute;
        top: 28px;
        left: 55px;
        height: 41px;
      }
    }
    .page-right {
      width: 38%;
      max-width: 430px;
      height: 100%;
      .el-carousel,
      .el-carousel__container {
        width: 100%;
        height: 100% !important;
        .el-carousel__arrow {
          display: none;
        }
        .el-carousel__item {
          &:nth-of-type(2) {
            .item-text {
              img {
                height: 28px;
                padding-left: 25%;
              }
              &:last-child img {
                padding-right: 25%;
              }
            }
          }
          &:nth-of-type(3) {
            .item-text {
              text-align: center !important;
              img {
                padding: 0 !important;
              }
            }
          }
        }
      }
      .el-carousel__indicators {
        .el-carousel__button {
          width: 7px;
          height: 7px;
          border-radius: 50%;
          opacity: 1;
        }
        .el-carousel__indicator.is-active button {
          background-color: #3370ff;
        }
      }

      .carousel-item {
        position: relative;
        height: 100%;
        .item-bg {
          width: 100%;
          height: 100%;
        }
        .item-content {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          padding-top: 40%;
          display: flex;
          flex-direction: column;
          align-items: center;
          .item-pic {
            width: 100%;
            max-width: 414px;
          }
          .item-text {
            width: 100%;
            text-align: left;
            margin-bottom: 10px;
            img {
              padding-left: 18%;
              height: 30px;
            }
            &:last-child {
              text-align: right;
              img {
                padding-right: 18%;
                height: 30px;
              }
            }
          }
        }
      }
    }
  }
</style>
