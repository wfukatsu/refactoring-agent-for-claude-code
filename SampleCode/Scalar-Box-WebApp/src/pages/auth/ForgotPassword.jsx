import * as React from 'react';
import Button from '@mui/material/Button';
import { styled } from '@mui/material/styles';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';


import { Visibility, VisibilityOff } from "@mui/icons-material";
import { Divider, IconButton, InputAdornment } from "@mui/material";

import LockIcon from '@mui/icons-material/Lock';

import TextField from '@mui/material/TextField';
import OTPInput from './OtpComponent';
import { BASE_URL } from '../../utils/constants';
import { axiosPrivate } from '../../api/axios';
import SuccessPopUp from '../../common/SuccessPopUp/SuccessPopUp';
import ErrorPopUp from '../../common/ErrorPopUp/ErrorPopUp';

import LoadingButton from '@mui/lab/LoadingButton';

import { useTranslation } from "react-i18next";

const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  '& .MuiDialogContent-root': {
    padding: theme.spacing(2),
  },
  '& .MuiDialogActions-root': {
    padding: theme.spacing(1),
  },
}));

export default function ForgotPassword() {
  const [open, setOpen] = React.useState(false);
  const [otp, setOtp] = React.useState('');

  const [email, SetEmail] = React.useState("");
  const [password, setPassword] = React.useState('');
  const [confirmPassword, setConfirmPassword] = React.useState('');

  const [helpMessageEmail, setHelperTextEmail] = React.useState("");
  const [helpMessageOtp, setHelperTextOtp] = React.useState("");
  const [errorMessage, setErrorMessage] = React.useState('');

  const [loading, setLoading] = React.useState(false);

  const [isValidEmail, setIsValidEmail] = React.useState(false);

  const [disabledButtonEmail, setDisabledButtonEmail] = React.useState(true);
  const [resetOtpBool, setResetOtpBool] = React.useState(true);
  const [passwordBool, setPasswordBool] = React.useState(true);

  const [showPassword, setShowPassword] = React.useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = React.useState(false);

  const [showSuccessPopup, setShowSuccessPopup] = React.useState(false);
  const [showErrorPopup, setShowErrorPopup] = React.useState(false);
  const [msg,setMsg] = React.useState('');

  const {t} = useTranslation();

  const handleClickShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const handleMouseDownPassword = (event) => {
    event.preventDefault();
  };

  const handleClickShowPasswordTwo = () => {
    setShowConfirmPassword(!showConfirmPassword);
  };

  const handleMouseDownPasswordTwo = (event) => {
    event.preventDefault();
  };

  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
    setIsValidEmail(false);
    SetEmail('');
    setOtp('');
    setPassword('');
    setConfirmPassword('');

    setHelperTextOtp('');
    setHelperTextEmail('');
    setErrorMessage('');
    setShowPassword(false);
    setShowConfirmPassword(false);

    setDisabledButtonEmail(true);
    setResetOtpBool(true);
    setPasswordBool(true);
    
    setLoading(false);
  };

  const CheckEmail = () => {
    setIsValidEmail(!isValidEmail);
    isFirstRender.current = false;
    setOtp('');
    setPassword('');
    setConfirmPassword('');
    setHelperTextOtp('');
    setErrorMessage('');
    setShowPassword(false);
    setShowConfirmPassword(false);
    setMsg('');
    setPasswordBool(true);
    setLoading(false);
  };

  const applyButton = () => {
    if (isValidEmail) {
      // console.log("ok first or not")
      ForgotPasswordMethod();
    } else {
      // console.log("this is");
      setLoading(true);
      SendResetOtpMethod();

    }
  };


  ///
  const customOr = (a, b) => {
    return a || b;
};

  ///////////////////////////////
  // validations 

  const handleChangeEmail = (e) => {
    const inputValue = e.target.value;
    SetEmail(inputValue);

    const isValidEmail = /^\S+@\S+\.\S+$/.test(inputValue);
    const hasSpecialCharacters = /[^\w\s@.-]/.test(inputValue);

    if (inputValue.trim() === "") {
      setHelperTextEmail(t("forgotPasswordDaiLogEmailErOneText"));
      setDisabledButtonEmail(true);
    } else if (!isValidEmail) {
      setHelperTextEmail(t("forgotPasswordDaiLogEmailErTwoText"));
      setDisabledButtonEmail(true);
    } else if (hasSpecialCharacters) {
      setHelperTextEmail(t("forgotPasswordDaiLogEmailErTwoText"));
      setDisabledButtonEmail(true);
    } else {
      setHelperTextEmail("");
      setDisabledButtonEmail(false);
    }
  };

  
// for password validation
const handlePasswordChange = (e) => {
  const { value } = e.target;
  setPassword(value);

  // Check if confirm password doesn't match
  if (confirmPassword !== value) {
    setErrorMessage(t("forgotPasswordDaiLogPasswordErOneText"));
    setPasswordBool(true);
  } else if (value.length < 8 || value.length > 15) {
    setErrorMessage(t("forgotPasswordDaiLogPasswordErTwoText"));
    setPasswordBool(true);
  } else {
    setErrorMessage('');
    setPasswordBool(false);
  }
};

// for confirm password
const handleConfirmPasswordChange = (e) => {
  const { value } = e.target;
  setConfirmPassword(value);

  // Validate password match as the user types
  if (password !== value) {
    setErrorMessage(t("forgotPasswordDaiLogPasswordErOneText"));
    setPasswordBool(true);
  } else if (value.length < 8 || value.length > 15) {
    setErrorMessage(t("forgotPasswordDaiLogPasswordErTwoText"));
    setPasswordBool(true);
  } else {
    setErrorMessage('');
    setPasswordBool(false);
  }
};




  // Call validateFields whenever any field changes
  const isFirstRender = React.useRef(true);

  React.useEffect(() => {
    if (isFirstRender.current) {
      isFirstRender.current = false;
      return;
    }

    if (/^\d{0,6}$/.test(otp)) {
      if (otp.length !== 6) {
        setHelperTextOtp(t("forgotPasswordDaiLogOtpErOneText"));
        setResetOtpBool(true);
      } else {
        setHelperTextOtp('');
        setResetOtpBool(false);
      }
    } else {
      setHelperTextOtp(t("forgotPasswordDaiLogOtpErTwoText"));
      setResetOtpBool(true);
    }
  }, [otp]);

  ////
  // network calls 
  const SendResetOtpMethod = () =>{
    const url = `${BASE_URL}/box/user/sendResetPasswordOTP?email=${email}`;

    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data.data);
        CheckEmail();
      })
      .catch((error) => {
        // console.error(error.response.data.message);
        setHelperTextEmail(error.response.data.message);
      }).finally(()=>{
        setLoading(false);
      });
  };

  const ForgotPasswordMethod = ()=>{

    const url = `${BASE_URL}/box/user/forgotPassword`;

    const header = {
      "otp": otp,
      "userEmail": email,
      "newPassword": password
    };
    
    axiosPrivate
      .post(url,header)
      .then((response) => {
        console.log(response.data.message);
        setMsg(response.data.message);
        setShowSuccessPopup(true);
        handleClose();
      })
      .catch((error) => {  
        // console.error(error.response.data.message);
        // setMsg(error.response.data.message);
        setHelperTextOtp(error.response.data.message);
      });

  };



  return (
    <React.Fragment>
      <div style={{display: "flex", justifyContent: "flex-end",}}>
      <Button style={{ fontSize: "14px" ,textTransform: 'none'}}
        // disabled={true}
        onClick={handleClickOpen}
      >{t("loginScreenForgotPassWordText")}
      </Button>
      </div>
      <BootstrapDialog
        onClose={handleClose}
        aria-labelledby="customized-dialog-title"
        open={open}>
          
        <div style={{ height: '20px' }}></div>

        <div style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center" }}>
          <div style={{ display: "flex", justifyContent: "center", alignItems: "center", width: "60px", height: "60px", borderRadius: "50%", backgroundColor: " #2673bb" }}>
            <div style={{ display: "flex", justifyContent: "center", alignItems: "center", width: "45px", height: "45px", borderRadius: "50%", border: "white solid" }}>
              <LockIcon style={{ fontSize: "24px", color: "white" }} />
            </div>
          </div>
        </div>




        <DialogTitle sx={{ textAlign: "center", m: 0, p: 2 }} id="customized-dialog-title">
          {t("forgotPasswordDaiLogTitleText")}
        </DialogTitle>

        <p style={{ textAlign: "center", fontSize: "13px" }}>{isValidEmail ?
         <p style={{paddingLeft:"10px",paddingRight:"10px"}}>{t("forgotPasswordDaiLogInfoTwoText")}</p>
          :
          t("forgotPasswordDaiLogInfoText")}</p>
        <DialogContent>
          {/* <div style={{ width: '450px', height: '0px' }}></div> */}
          {isValidEmail ? <div></div> :
            <div style={{width:"380px"}}>
              <TextField
                size='small'
                id="standard-basic"
                placeholder={t("forgotPasswordDaiLogEmailHintText")}
                variant="outlined"
                value={email}
                onChange={handleChangeEmail}
                disabled={isValidEmail}
                fullWidth
              />
              {helpMessageEmail && (
                <p style={{ color: "red", fontSize: "12px" }}>
                  {helpMessageEmail}
                </p>
              )}
            </div>}
          <div style={{ height: "10px" }}></div>
          {isValidEmail &&
            <div style={{
              // backgroundColor:"green",

            }}>

              <div style={{width: "100%", display: "flex", justifyContent: "center", alignItems: "center"}}>
              <OTPInput value={otp} onChange={setOtp} length={6} />
              </div>
              {helpMessageOtp && (
                <div style={{width: "50%", display: "flex", justifyContent: "center", alignItems: "center"}} >
                  <p style={{ color: "red", fontSize: "12px" }}>
                  {helpMessageOtp}
                </p></div>
              )}
              <div style={{ height: "20px" }}></div>

              <div style={{width: "100%", display: "flex", justifyContent: "center", alignItems: "center"}}>
              <TextField
              sx={{width:"380px"}}
                size='small'
                placeholder={t("forgotPasswordDaiLogPasswordHintText")}
                variant="outlined"
                fullWidth
                value={password}
                onChange={handlePasswordChange}
                type={showPassword ? "text" : "password"}
                inputProps={{
                  maxLength: 15,
                }}
                InputProps={{
                  disableUnderline: true,
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowPassword}
                        onMouseDown={handleMouseDownPassword}
                        edge="end"
                      >
                        {showPassword ? <Visibility fontSize='small'/> : <VisibilityOff fontSize='small'/>}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
              </div>

              <div style={{ height: "10px" }}></div>
              <div style={{width: "100%", display: "flex", justifyContent: "center", alignItems: "center"}}>
              <TextField
              sx={{width:"380px"}}
                size='small'
                placeholder={t("forgotPasswordDaiLogConfirmPasswordHintText")}
                variant="outlined"
                fullWidth
                type={showConfirmPassword ? "text" : "password"}
                value={confirmPassword}
                onChange={handleConfirmPasswordChange}
                inputProps={{
                  maxLength: 15,
                }}
                InputProps={{
                  disableUnderline: true,
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowPasswordTwo}
                        onMouseDown={handleMouseDownPasswordTwo}
                        edge="end"
                      >
                        {showConfirmPassword ? <Visibility fontSize='small'/> : <VisibilityOff fontSize='small' />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
              </div>
              {errorMessage && 
              // <div style={{width: "100%", display: "flex", justifyContent: "center", alignItems: "center",backgroundColor:"green"}}>
                <p style={{ color: "red", fontSize: "12px" }}>{errorMessage}</p>
              // </div>
              }
              

            </div>}
        </DialogContent>
        <DialogActions>
          {isValidEmail ?
            <div style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center",padding:"10px" }}>
              <Button
                style={{ textTransform: 'none' }}
                autoFocus
                variant="contained"
                disabled={customOr(resetOtpBool, passwordBool)}
                onClick={applyButton}>
                <p>{t("forgotPasswordDaiLogUpdateButtonText")}</p>
              </Button>
              <div style={{width:"30px"}}></div>
              <Button
                style={{ textTransform: 'none' }}
                autoFocus
                variant="outlined"
                onClick={CheckEmail}>
                {t("forgotPasswordDaiLogBackButtonText")}
              </Button>
            </div>
            :
            <div style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center",padding:"10px" }}>
              {/* <Button
                style={{ textTransform: 'none' }}
                autoFocus
                variant="contained"
                disabled={disabledButtonEmail}
                onClick={applyButton}
                >
                Sent OTP to Email
              </Button> */}
              <LoadingButton
              style={{textTransform: 'none'}}
          size="medium"
          disabled={disabledButtonEmail}
          onClick={applyButton}
          loading={loading}
          variant="contained"
          
        >
          <span>{t("forgotPasswordDaiLogSendOtpButtonText")}</span>
        </LoadingButton>
              <div style={{width:"30px"}}></div>
              <Button variant="outlined" style={{ textTransform: 'none' }} onClick={handleClose}>{t("forgotPasswordDaiLogCancelButtonText")}</Button>
            </div>
          }
          
        </DialogActions>
      </BootstrapDialog>
          {showSuccessPopup && (
      <SuccessPopUp
          iconColor="primary"
          iconSize={50}
          title={msg}
          open={showSuccessPopup}
          handleClose={() => {setShowSuccessPopup(false);setMsg('');}}
        />
      )}
      {showErrorPopup && (
        <ErrorPopUp
          iconColor="error"
          iconSize={50}
          title={msg}
          open={showErrorPopup}
          handleClose={() => setShowErrorPopup(false)}
        />
      )}

    </React.Fragment>
  );
}
