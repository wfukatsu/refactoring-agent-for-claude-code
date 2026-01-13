import { Box, SvgIcon, SvgIconProps } from "@mui/material";
import React from "react";

export interface IconProps extends SvgIconProps {}

const EditIconSvg = (props: IconProps) => {
  return (
    <SvgIcon style={{ fill: "none" }} {...props}><path fill="#00132B" d="M.666 23.333v-4.512L18.253 1.24a2.25 2.25 0 0 1 .668-.424 2 2 0 0 1 .767-.15c.267 0 .526.047.776.142.251.095.473.246.666.453l1.628 1.648c.207.193.355.416.443.667.088.251.132.503.132.754 0 .268-.046.524-.138.768a1.89 1.89 0 0 1-.437.668L5.178 23.333H.667ZM19.402 6.26l1.93-1.918-1.674-1.674-1.918 1.93 1.662 1.662Z"/></SvgIcon>
  );
};

export default EditIconSvg;