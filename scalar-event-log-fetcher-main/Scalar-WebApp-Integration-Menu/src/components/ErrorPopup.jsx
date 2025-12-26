import Button from "@mui/material/Button";
import { styled } from "@mui/material/styles";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import ErrorIcon from "@mui/icons-material/Error";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";

const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  "& .css-1t1j96h-MuiPaper-root-MuiDialog-paper": {
    padding: theme.spacing(1),
    borderRadius: "20px",
  },
  "& .MuiDialogContent-root": {
    padding: theme.spacing(5),
  },
  "& .MuiDialogActions-root": {
    padding: theme.spacing(1),
    justifyContent: "center",
  },
}));
const ErrorPopup = (props) => {
  const { iconColor, iconSize, title, open, handleClose } = props;
  const {t,i18n} = useTranslation();


  const [secondsRemaining, setSecondsRemaining] = React.useState(5);

  useEffect(() => {
    let timer;
    
    if (open && secondsRemaining > 0) {
      timer = setTimeout(() => {
        setSecondsRemaining(prevSeconds => prevSeconds - 1);
      }, 1000);
    } else {
      handleClose();
    }

    return () => clearTimeout(timer);
  }, [open, handleClose, secondsRemaining]);



  return (
    <div>
      <BootstrapDialog
        onClose={handleClose}
        aria-labelledby="customized-dialog-title"
        open={open}
      >
        <DialogContent dividers>
          <div className="flex flex-col gap-2 items-center justify-center ">
            <ErrorIcon color={iconColor} sx={{ fontSize: iconSize }} />
            <h2 className="text-xl font-bold">{title}</h2>
            {
              i18n.language == "ja" ?
              <p>{secondsRemaining} {t("closingTimerMessageText")}</p> 
              :
             <p>{t("closingTimerMessageText")} {secondsRemaining} {t("secondsText")}... </p>
            }
          </div>
        </DialogContent>
        <DialogActions>
          <Button autoFocus onClick={handleClose}>
          {t("closeNowText")}
          </Button>
        </DialogActions>
      </BootstrapDialog>
    </div>
  );
};
export default ErrorPopup;
