// SuccessPopUp.js
import React, { useState } from "react";
import Button from "@mui/material/Button";
import { styled } from "@mui/material/styles";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import HelpIcon from '@mui/icons-material/Help';
import Divider from '@mui/material/Divider';
import { useEffect } from "react";

import SuccessPopUp from "../SuccessPopUp/SuccessPopUp";

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

const DeletePopUp = (props) => {
  const { iconColor, iconSize, title, open, handleClose, handleDelete } = props;

  const {t,i18n} = useTranslation();
  

  return (
    <div>
      <BootstrapDialog
        onClose={handleClose}
        aria-labelledby="customized-dialog-title"
        open={open}
      >
        <DialogContent dividers>
          <div className="flex flex-col gap-2 items-center justify-center ">
            <HelpIcon  sx={{ fontSize: iconSize ,color:"red" }} />
            <h1 className="text-xl font-bold">{title}</h1>
            <p>{t("areYouSureWantToDeleteText")}</p>
          </div>
        </DialogContent>
        <DialogActions
          sx={{
            "& .css-10fk08f-MuiModal-root-MuiDialog-root .MuiDialogActions-root":
              {
                padding: "8px",
                alignItems: "center",
                justifyContent: "center",
              },
          }}
        >
          <Button autoFocus onClick={()=>{
            
            handleDelete();
          }}>
            {t("yesTextConfirmText")}
          </Button>
          <Divider orientation="vertical" variant="middle" flexItem />
          <Button autoFocus onClick={handleClose}>
            {t("cancelConfimText")}
          </Button>
        </DialogActions>
      </BootstrapDialog>
    </div>
  );
};

export default DeletePopUp;
