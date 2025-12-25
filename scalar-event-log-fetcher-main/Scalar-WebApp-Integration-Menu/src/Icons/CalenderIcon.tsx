import React from "react";
import { Box, SvgIcon, SvgIconProps } from "@mui/material";

export interface CalenderIconProps extends SvgIconProps {}

const CalenderIcon = (props: CalenderIconProps) => {
  return (
    <SvgIcon style={{ fill: "none" }} {...props}><path fill="#0061D5" d="M5.308 21.5c-.505 0-.933-.175-1.283-.525a1.745 1.745 0 0 1-.525-1.283V6.308c0-.505.175-.933.525-1.283.35-.35.778-.525 1.283-.525h1.384V3.154c0-.22.074-.402.22-.55a.745.745 0 0 1 .55-.22.749.749 0 0 1 .769.77V4.5h7.577V3.135c0-.213.071-.391.215-.535a.726.726 0 0 1 .535-.215c.212 0 .39.071.534.215a.726.726 0 0 1 .216.535V4.5h1.384c.505 0 .933.175 1.283.525.35.35.525.778.525 1.283v13.384c0 .505-.175.933-.525 1.283-.35.35-.778.525-1.283.525H5.308Zm0-1.5h13.384a.294.294 0 0 0 .212-.096.294.294 0 0 0 .096-.212v-9.384H5v9.384c0 .077.032.148.096.212a.294.294 0 0 0 .212.096Z"/></SvgIcon>
  );
};

export default CalenderIcon;