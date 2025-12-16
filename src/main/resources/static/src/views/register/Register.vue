<template>
  <div class="register-container">
    <div class="left-section">
      <img src="/Vector 1.png" class="bg-line line-top" alt="bg-line">
      <img src="/Vector 2.png" class="bg-line line-bottom" alt="bg-line">

      <div class="content-wrapper">
        <div class="image-gallery">
          <div class="photo-wrapper img-1">
            <img src="/unsplash 1.png" alt="Camera">
          </div>
          <div class="photo-wrapper img-2">
            <img src="/unsplash 2.png" alt="Photographer">
          </div>
          <div class="photo-wrapper img-3">
            <img src="/unsplash 3.png" alt="Lens">
          </div>
        </div>

        <div class="main-icon-wrapper">
          <img src="/mainIcon.png" class="main-icon" alt="Main Icon">
        </div>

        <div class="slogan">
          CAPTURE MOMENTS,<br>
          TRADE SMART
        </div>
      </div>
    </div>

    <div class="right-section">
      <div class="form-wrapper">
        <h1 class="title">Create account</h1> 
        <p class="subtitle">Join us</p>

        <div class="form-body">
          <div class="input-group">
            <input 
              v-model="act" 
              class="styled-input" 
              placeholder="your account" 
              type="text"
              @keyup.enter="registerFunc"
            >
          </div>
          
          <div class="input-group">
            <input 
              v-model="name" 
              class="styled-input" 
              placeholder="username" 
              type="text"
              @keyup.enter="registerFunc"
            >
          </div>

          <div class="input-group">
            <input 
              v-model="pwd" 
              class="styled-input" 
              placeholder="password" 
              type="password"
              @keyup.enter="registerFunc"
            >
          </div>

          <div class="input-group">
            <input 
              v-model="pwdConfirm" 
              class="styled-input" 
              placeholder="password again" 
              type="password"
              @keyup.enter="registerFunc"
            >
          </div>

          <button class="register-btn" @click="registerFunc">
            Create
          </button>

          <div class="footer-links">
            <span class="create-account" @click="toDoLogin">Already have an account? <span class="login-link-text">Login</span></span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const DELAY_TIME = 1300;
import request from "@/utils/request.js";
import md5 from 'js-md5';

export default {
    name: "Register",
    data() {
        return {
            act: '', 
            pwd: '', 
            pwdConfirm: '', 
            name: '' 
        }
    },
    methods: {
        toDoLogin() {
            this.$router.push('/login');
        },
        async registerFunc() {
            if (!this.act || !this.pwd || !this.pwdConfirm || !this.name) {
                this.$swal.fire({
                    title: 'Validation Error',
                    text: 'Account, username, or password cannot be empty',
                    icon: 'warning', 
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                return;
            }
            if (this.pwd !== this.pwdConfirm) {
                this.$swal.fire({
                    title: 'Validation Error',
                    text: 'Passwords do not match',
                    icon: 'error',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                return;
            }
            
            const hashedPwd = md5(md5(this.pwd));
            const paramDTO = { 
                userAccount: this.act, 
                userPwd: hashedPwd, 
                userName: this.name 
            };
            
            try {
                const { data } = await request.post(`user/register`, paramDTO);
                
                if (data.code !== 200) {
                    this.$swal.fire({
                        title: 'Registration Failed',
                        text: data.msg,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: DELAY_TIME,
                    });
                    return;
                }
                
                this.$swal.fire({
                    title: 'Registration Successful',
                    text: 'Redirecting to login page...',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
                
                setTimeout(() => {
                    this.$router.push('/login');
                }, DELAY_TIME);
            } catch (error) {
                console.error('Registration request error:', error);
                
                this.$swal.fire({
                    title: 'Registration Error',
                    text: 'An error occurred during registration. Please try again.',
                    icon: 'error',
                    showConfirmButton: false,
                    timer: DELAY_TIME,
                });
            }
        }
    }
};
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500&family=Russo+One&display=swap');

$primary-blue: #94a9ff; 
$text-grey: #666;
$input-border: #000; 

.register-container {
    display: flex;
    width: 100vw;
    height: 100vh;
    overflow: hidden;
    font-family: 'Poppins', sans-serif;
    background: white;
}

.left-section {
    flex: 1;
    background-color: #9EAEFF; 
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: center; 
    align-items: center;
    overflow: hidden;

    @media (max-width: 768px) {
        display: none;
    }
}
.bg-line {
    position: absolute;
    pointer-events: none;
    z-index: 0; 
    opacity: 0.6;
    width: 100%; 
    height: auto; 
}
.line-top {
    top: 30px; 
    left: 80px; 
    transform: translateY(10%) translateX(-10%); 
    width: 100%; 
    max-width: none; 
}
.line-bottom {
    bottom: 10px; 
    right: 90px; 
    transform: translateY(0%) translateX(10%); 
    width: 100%; 
    max-width: none;
}
.content-wrapper {
    position: relative;
    z-index: 1; 
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center; 
    padding-top: 0; 
}
.image-gallery {
    position: relative;
    width: 100%;
    height: 350px; 
    margin-top: 20px; 
}
.photo-wrapper {
    position: absolute;
    background: none !important;
    padding: 0 !important;
    border: none !important;
    box-shadow: none !important;
    z-index: 99;
    
    img {
        display: block;
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 4px; 
        box-shadow: none; 
    }
}
.img-1 { 
    width: 170px; 
    height: 220px; 
    left: 8%; 
    top: 20%; 
    transform: rotate(-3deg); 
}
.img-2 { 
    width: 170px; 
    height: 220px; 
    left: 48%; 
    top: -2%; 
    transform: translateX(-50%) rotate(2deg); 
}
.img-3 { 
    width: 170px; 
    height: 220px; 
    right: 13%; 
    top: 30%; 
    transform: rotate(-3deg); 
}
.main-icon-wrapper {
    margin-top: -100px; 
    margin-bottom: -10px;
    display: flex;
    justify-content: center;
}
.main-icon {
    width: 350px; 
    height: auto;
    object-fit: contain;
    opacity: 0.8;
}
.slogan {
    margin-top: auto;
    margin-bottom: 100px; 
    color: white;
    font-size: 2.5rem;
    font-weight: 500;
    text-align: center;
    line-height: 1.3;
    letter-spacing: 1px;
}


.right-section {
    flex: 1;
    background-color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 40px;
}

.form-wrapper {
    width: 100%;
    max-width: 380px;
    text-align: center;
}

.title {
    font-family: 'Russo One', sans-serif;
    font-size: 2.5rem;
    color: $primary-blue;
    margin-bottom: 10px;
    letter-spacing: 1px;
    font-style: italic;
}

.subtitle {
    color: $text-grey;
    margin-bottom: 30px; 
    font-size: 0.95rem;
    font-weight: 400;
}

.form-body {
    width: 100%;
}

.input-group {
    margin-bottom: 15px; 
    text-align: left;
}

.styled-input {
    width: 100%;
    padding: 16px 20px;
    border: 1.5px solid $input-border;
    border-radius: 4px;
    font-size: 0.95rem;
    outline: none;
    font-family: 'Poppins', sans-serif;
    color: #333;
    transition: all 0.3s;

    &::placeholder {
        color: #777;
    }

    &:focus {
        border-color: $primary-blue;
        box-shadow: 0 0 0 3px rgba(148, 169, 255, 0.1);
    }
}

.register-btn {
    width: 100%;
    padding: 16px;
    background-color: $primary-blue;
    color: white;
    border: none;
    border-radius: 6px;
    font-family: 'Russo One', sans-serif;
    font-size: 1.2rem;
    font-style: italic; 
    letter-spacing: 2px;
    cursor: pointer;
    margin-top: 25px; 
    margin-bottom: 40px; 
    transition: background-color 0.3s, transform 0.1s;
    box-shadow: 0 5px 15px rgba(148, 169, 255, 0.4);

    &:hover {
        background-color: darken($primary-blue, 5%);
    }
    
    &:active {
        transform: translateY(2px);
    }
}

.footer-links {
    text-align: center;
}

.create-account {
    color: $text-grey;
    font-size: 0.9rem;
    cursor: pointer; 
    transition: color 0.3s;

    &:hover {
        color: #000;
    }
}

.login-link-text {
    text-decoration: underline;
    font-weight: 500;
}

@media (max-width: 768px) {
    .register-container {
        flex-direction: column;
        height: auto;
        min-height: 100vh;
    }
    
    .right-section {
        padding: 40px 30px;
    }
}
</style>