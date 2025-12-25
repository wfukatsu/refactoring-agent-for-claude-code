import Button from "@mui/material/Button";
import { styled } from "@mui/material/styles";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import Typography from "@mui/material/Typography";
import { Fragment, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { BASE_URL } from "../../../utils/constants";

import useAxiosPrivate from "../../hooks/useAxiosPrivate";

const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  "& .MuiDialogContent-root": {
    padding: theme.spacing(2),
  },
  "& .MuiDialogActions-root": {
    padding: theme.spacing(1),
  },
}));

export default function OpenPreview() {
  const [open, setOpen] = useState(false);
  const [boxAccessToken, setBoxAccessToken] = useState(null);
  const axiosPrivate = useAxiosPrivate();

  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  }; 

  const fetchBoxToken = async () => {
    const response = await axiosPrivate.post(
      `${BASE_URL}/user/userSignIn/abhisheks@kanzencs.com`
    );
    if (response.status === 200) {
      const { accessToken } = { ...response.data.data };
      console.log(accessToken, "BOX TOKEN");
      setBoxAccessToken(accessToken);
    } else {
      throw Error("ERROR :: fetchBoxToken");
    }
  };

  fetchBoxToken().catch((error) => {
    console.log(`${error}`);
  });

  useEffect(() => {
    const script = document.createElement("script");
    script.src =
      "https://cdn01.boxcdn.net/platform/preview/2.54.0/en-US/preview.js";
    script.async = true;

    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);

  useEffect(() => {
    try {
      if (
        window.Box &&
        typeof window.Box.Preview === "function" &&
        boxAccessToken !== null
      ) {
        const preview = new window.Box.Preview();
        console.log(preview, "PREVIEW");
        preview.show("1459178668579", boxAccessToken, {
          container: ".preview-container",
          showDownload: true,
        });
      }
    } catch (error) {
      console.error("Error occurred while initializing Box Preview:", error);
    }
  }, [boxAccessToken]);

  return (
    <Fragment>
      <Button variant="outlined" onClick={handleClickOpen}>
        Open dialog
      </Button>
      <BootstrapDialog
        onClose={handleClose}
        aria-labelledby="customized-dialog-title"
        open={open}
      >
        <IconButton
          aria-label="close"
          onClick={handleClose}
          sx={{
            position: "absolute",
            right: 8,
            top: 8,
            color: (theme) => theme.palette.grey[500],
          }}
        >
          <CloseIcon />
        </IconButton>
        <DialogContent dividers>
          <div
            className="preview-container"
            style={{ height: "490px", width: "100%", border: "0px solid #eee" }}
          ></div>
        </DialogContent>
        {/* <DialogActions>
          <Button autoFocus onClick={handleClose}>
            Save changes
          </Button>
        </DialogActions> */}
      </BootstrapDialog>
    </Fragment>
  );
}
