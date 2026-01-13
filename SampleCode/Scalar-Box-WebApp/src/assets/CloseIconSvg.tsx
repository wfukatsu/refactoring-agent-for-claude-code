import { Box, SvgIcon, SvgIconProps } from "@mui/material";
import React from "react";

export interface CloseIconProps extends SvgIconProps {}

const CloseIcon = (props: CloseIconProps) => {
  return (
    <SvgIcon {...props}><path d="m12 12.992-5.073 4.774a.749.749 0 0 1-.522.2.73.73 0 0 1-.532-.2.656.656 0 0 1-.217-.495c0-.195.072-.36.217-.496L10.946 12 5.873 7.225a.662.662 0 0 1-.212-.491.645.645 0 0 1 .212-.5.74.74 0 0 1 .527-.205.74.74 0 0 1 .527.204L12 11.008l5.073-4.775a.748.748 0 0 1 .522-.2.73.73 0 0 1 .532.2.656.656 0 0 1 .217.496c0 .195-.072.36-.217.496L13.054 12l5.073 4.775c.138.13.21.294.212.491a.646.646 0 0 1-.212.5.74.74 0 0 1-.527.205.74.74 0 0 1-.527-.205L12 12.992Z"/></SvgIcon>
  );
};

export default CloseIcon;