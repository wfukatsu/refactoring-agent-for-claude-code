import { styled } from "@mui/material/styles";
import { useEffect } from "react";

// export default function Main(props) {
//   const drawerWidth = 309;

//   useEffect(() => {}, []);

//   console.log("PROPS :: ", props.open);
//   const Layout = styled("main")(({ theme }) => ({
//     marginLeft: 82,
//     flexGrow: 1,
//     padding: theme.spacing(2),
//     paddingTop: 15,
//     backgroundColor: "#ffff",
//     transition: theme.transitions.create("margin", {
//       easing: theme.transitions.easing.sharp,
//       duration: theme.transitions.duration.leavingScreen,
//     }),
//     ...(props.open && {
//       marginLeft: `${drawerWidth}px`,
//       transition: theme.transitions.create("margin", {
//         easing: theme.transitions.easing.easeOut,
//         duration: theme.transitions.duration.enteringScreen,
//       }),
//     }),
//   }));

//   return <Layout>{props.children}</Layout>;
// }
